package com.zolp.custhumb.infra.domain.gcs;

import com.fasterxml.jackson.databind.JsonNode;
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
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GcsSignedUrlService {

    private final Storage storage;

    public GcsSignedUrlService(@Value("${gcp.credentials.path}") Resource keyFile) throws IOException {
        this.storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(keyFile.getInputStream()))
                .build()
                .getService();
    }

    public URL generateUploadUrl(String bucketName, String objectName, String contentType, int expireMinutes, Long userId, String timestamp) {
        String[] parts = objectName.split("/", 2);
        String type = parts[0];
        String fileName = parts.length > 1 ? parts[1] : "unknown";

        String fullPath = String.format("%d/%s/%s__%s", userId, type, timestamp, fileName);

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

    public String requestThumbnailToAiServer(String videoUrl, String videoPrompt, String imageUrl, String imagePrompt, String thumbnailUploadUrl) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> requestBody = Map.of(
                    "video_url", videoUrl,
                    "video_prompt", videoPrompt,
                    "reference_image_url", imageUrl,
                    "reference_image_prompt", imagePrompt,
                    "thumbnail_upload_url", thumbnailUploadUrl
            );

            String requestJson = objectMapper.writeValueAsString(requestBody);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://203.252.147.202:8100/api/v1.0/generate_thumbnail_prompt"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode responseJson = objectMapper.readTree(response.body());
            return responseJson.get("thumbnail_url").asText();
        } catch (Exception e) {
            throw new RuntimeException("AI 서버 요청 실패", e);
        }
    }

    public Map<String, Object> getEarliestMediaData(String userId) {
        List<Blob> videoBlobs = listBlobs("custhumb-bucket", userId + "/video/");
        List<Blob> imageBlobs = listBlobs("custhumb-bucket", userId + "/image/");

        String earliestVideo = findEarliestByTimestamp(videoBlobs, "mp4");
        String earliestImage = findEarliestByTimestamp(imageBlobs, "png");

        String videoTimestamp = extractTimestamp(earliestVideo);
        String imageTimestamp = extractTimestamp(earliestImage);

        String videoPromptFile = findMatchingPrompt(videoBlobs, videoTimestamp, "txt");
        String imagePromptFile = findMatchingPrompt(imageBlobs, imageTimestamp, "txt");

        String videoPrompt = readBlobContent("custhumb-bucket", userId + "/video/" + videoPromptFile);
        String imagePrompt = readBlobContent("custhumb-bucket", userId + "/image/" + imagePromptFile);

        Map<String, Object> result = new HashMap<>();
        result.put("videoUrl", buildPublicUrl(userId + "/video/" + earliestVideo));
        result.put("videoPrompt", videoPrompt);
        result.put("imageUrl", buildPublicUrl(userId + "/image/" + earliestImage));
        result.put("imagePrompt", imagePrompt);
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
                .min(Comparator.comparing(blob -> extractTimestamp(blob.getName())))
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
        return "https://storage.googleapis.com/custhumb-bucket/" + path;
    }
}
