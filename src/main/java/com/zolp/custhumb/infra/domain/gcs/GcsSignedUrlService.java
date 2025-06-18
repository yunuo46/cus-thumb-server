package com.zolp.custhumb.infra.domain.gcs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.google.cloud.storage.Blob;

@Service
public class GcsSignedUrlService {

    private final Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String gcsBucketName;

    @Value("${ai.server.thumbnail-generate-url}")
    private String aiServerThumbnailGenerateUrl;

    @Value("${ai.server.thumbnail-edit-url}")
    private String aiServerThumbnailEditUrl;


    public GcsSignedUrlService(@Value("${gcp.credentials.path}") Resource keyFile) throws IOException {
        this.storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(keyFile.getInputStream()))
                .build()
                .getService();
    }

    public URL generateUploadUrl(String bucketName, String objectName, String contentType, int expireMinutes, Long userId, String timestamp, Long idx) {
        String[] parts = objectName.split("/", 2);
        String type = parts[0];
        String fileName = parts.length > 1 ? parts[1] : "unknown";

        String fullPath = String.format("%d/%s/%s__%d_%s", userId, type, timestamp, idx, fileName);

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, fullPath))
                .setContentType(contentType)
                .build();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);

        return storage.signUrl(
                blobInfo,
                expireMinutes,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withExtHeaders(headers),
                Storage.SignUrlOption.withV4Signature()
        );
    }

    public List<String> requestThumbnailToAiServer(String videoUrl, String videoPrompt, List<String> thumbnailUploadUrls, List<String> referenceImageTypes, Long userId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("video_url", videoUrl);
            requestBody.put("video_prompt", videoPrompt);
            requestBody.put("reference_image_types", referenceImageTypes);
            requestBody.put("thumbnail_upload_urls", thumbnailUploadUrls);

            String requestJson = objectMapper.writeValueAsString(requestBody);

            System.out.println("생성된 AI 서버 요청 JSON 본문: " + requestJson);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(aiServerThumbnailGenerateUrl))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(1200))
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("AI 서버 응답 상태 코드: " + response.statusCode());
            System.out.println("AI 서버 응답 바디: " + response.body() );

            return findLatestThreeThumbnailUrls(userId);
        } catch (Exception e) {
            throw new RuntimeException("AI 서버 썸네일 생성 요청 실패", e);
        }
    }


    public String requestThumbnailEditToAiServer(String originalThumbnailUrl, String prompt, String newThumbnailUploadUrl, Long userId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, String> requestBody = Map.of(
                    "thumbnail_url", originalThumbnailUrl,
                    "prompt", prompt,
                    "thumbnail_upload_url", newThumbnailUploadUrl
            );

            String requestJson = objectMapper.writeValueAsString(requestBody);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(aiServerThumbnailEditUrl)) // 편집 URL 사용
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

            return findLatestThumbnailUrl(userId);
        } catch (Exception e) {
            throw new RuntimeException("AI 서버 편집 요청 실패", e);
        }
    }


    public List<String> findLatestThreeThumbnailUrls(Long userId) {
        String prefix = userId.toString() + "/thumbnail/";
        List<Blob> blobs = listBlobs(gcsBucketName, prefix);

        return blobs.stream()
                .filter(blob -> blob.getName().endsWith(".png"))
                .sorted(Comparator.comparing((Blob blob) -> extractTimestamp(blob.getName())).reversed())
                .limit(3)
                .map(blob -> buildPublicUrl(blob.getName()))
                .collect(Collectors.toList());
    }

    private String findLatestThumbnailUrl(Long userId) {
        String prefix = userId.toString() + "/thumbnail/";
        List<Blob> blobs = listBlobs(gcsBucketName, prefix);

        return blobs.stream()
                .filter(blob -> blob.getName().endsWith(".png"))
                .max(Comparator.comparing(blob -> extractTimestamp(blob.getName())))
                .map(blob -> buildPublicUrl(blob.getName()))
                .orElseThrow(() -> new RuntimeException("썸네일 PNG 파일이 없습니다."));
    }

    public Map<String, Object> getLatestMediaData(String userId) {
        List<Blob> videoBlobs = listBlobs(gcsBucketName, userId + "/video/");
        String earliestVideo = findEarliestByTimestamp(videoBlobs, "mp4");
        String videoTimestamp = extractTimestamp(earliestVideo);

        String videoPromptFile = findMatchingPrompt(videoBlobs, videoTimestamp, "txt");
        String videoPrompt = readBlobContent(gcsBucketName, userId + "/video/" + videoPromptFile);

        Map<String, Object> result = new HashMap<>();
        result.put("videoUrl", buildPublicUrl(userId + "/video/" + earliestVideo));
        result.put("videoPrompt", videoPrompt);
        return result;
    }

    private String readBlobContent(String bucketName, String objectPath) {
        Blob blob = storage.get(BlobId.of(bucketName, objectPath));
        if (blob == null) throw new RuntimeException("프롬프트 파일을 찾을 수 없습니다.");
        return new String(blob.getContent());
    }

    private List<Blob> listBlobs(String bucketName, String prefix) {
        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(prefix));
        return StreamSupport.stream(blobs.iterateAll().spliterator(), false)
                .filter(blob -> !blob.isDirectory())
                .collect(Collectors.toList());
    }

    private String findEarliestByTimestamp(List<Blob> blobs, String extension) {
        return blobs.stream()
                .filter(blob -> blob.getName().endsWith("." + extension))
                .max(Comparator.comparing(blob -> extractTimestamp(blob.getName())))
                .map(blob -> blob.getName().substring(blob.getName().lastIndexOf("/") + 1))
                .orElseThrow(() -> new RuntimeException("해당 형식의 파일이 없습니다."));
    }

    private String extractTimestamp(String blobName) {
        String fileName = blobName.substring(blobName.lastIndexOf("/") + 1);
        return fileName.split("__")[0];
    }

    private String findMatchingPrompt(List<Blob> blobs, String timestamp, String extension) {
        return blobs.stream()
                .filter(blob -> blob.getName().endsWith("." + extension))
                .filter(blob -> extractTimestamp(blob.getName()).equals(timestamp))
                .map(blob -> blob.getName().substring(blob.getName().lastIndexOf("/") + 1))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("프롬프트 파일이 없습니다."));
    }

    private String buildPublicUrl(String path) {
        return String.format("https://storage.googleapis.com/%s/%s", gcsBucketName, path);
    }
}
