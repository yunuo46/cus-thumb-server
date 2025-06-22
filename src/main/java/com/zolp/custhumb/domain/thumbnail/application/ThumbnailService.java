package com.zolp.custhumb.domain.thumbnail.application;

import com.zolp.custhumb.domain.thumbnail.dao.ThumbnailRepository;
import com.zolp.custhumb.domain.thumbnail.domain.Thumbnail;
import com.zolp.custhumb.domain.thumbnail.dto.request.CreateThumbnailRequest;
import com.zolp.custhumb.domain.thumbnail.dto.request.EditThumbnailRequest;
import com.zolp.custhumb.domain.thumbnail.dto.response.ThumbnailResponse;
import com.zolp.custhumb.domain.user.dao.UserRepository;
import com.zolp.custhumb.domain.user.domain.User;
import com.zolp.custhumb.infra.domain.gcs.GcsSignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

    private final ThumbnailRepository thumbnailRepository;
    private final UserRepository userRepository;
    private final GcsSignedUrlService gcsSignedUrlService;

    @Value("${gcp.storage.bucket-name}")
    private String gcsBucketName;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public List<ThumbnailResponse> create(Long userId, CreateThumbnailRequest createThumbnailRequest) {
        User user = userRepository.findById(userId).orElseThrow();

        String baseTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(FORMATTER);
        int numberOfThumbnailsToGenerate = createThumbnailRequest.types().size();

        List<String> thumbnailUploadUrls = new ArrayList<>();

        for (int idx = 0; idx < numberOfThumbnailsToGenerate; idx++) {
            String thumbnailObject = "thumbnail/" + ".png";

            URL uploadUrl = gcsSignedUrlService.generateUploadUrl(
                    gcsBucketName,
                    thumbnailObject,
                    "image/png",
                    15,
                    userId,
                    baseTimestamp,
                    idx
            );
            thumbnailUploadUrls.add(uploadUrl.toString());
        }

        Map<String, Object> data = gcsSignedUrlService.getLatestMediaData(String.valueOf(userId));
        List<String> thumbnailUrls = gcsSignedUrlService.requestThumbnailToAiServer(
                (String) data.get("videoUrl"),
                (String) data.get("videoPrompt"),
                thumbnailUploadUrls,
                createThumbnailRequest.types(),
                userId
        );

        List<ThumbnailResponse> responses = new ArrayList<>();

        for (String url : thumbnailUrls) {
            Thumbnail thumbnail = Thumbnail.builder()
                    .url(url)
                    .user(user)
                    .build();

            System.out.println("생성 및 저장될 썸네일 URL: " + url); // Log each URL

            Thumbnail saved = thumbnailRepository.save(thumbnail);
            responses.add(new ThumbnailResponse(saved.getId(), saved.getUrl(),saved.getCreatedAt()));
        }
        return responses;
    }

    public ThumbnailResponse edit(Long userId, EditThumbnailRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String url = thumbnailRepository.findById(request.id()).orElseThrow().getUrl();

        String baseTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(FORMATTER);
        String thumbnailObject = "thumbnail/" + ".png";

        URL newThumbnailUploadUrl = gcsSignedUrlService.generateUploadUrl(
                gcsBucketName,
                thumbnailObject,
                "image/png",
                15,
                userId,
                baseTimestamp,
                0
        );

        String maskingUrl = gcsSignedUrlService.findLatestMaskingUrl(userId);

        String newThumbnailUrl = gcsSignedUrlService.requestThumbnailEditToAiServer(
                url,
                request.prompt(),
                newThumbnailUploadUrl.toString(),
                maskingUrl,
                userId
        );

        Thumbnail editedThumbnail = Thumbnail.builder()
                .url(newThumbnailUrl)
                .user(user)
                .build();

        Thumbnail saved = thumbnailRepository.save(editedThumbnail);
        return new ThumbnailResponse(saved.getId(), saved.getUrl(),saved.getCreatedAt());
    }

    public List<ThumbnailResponse> getThumbnails(Long userId) {
        List<Thumbnail> thumbnails = thumbnailRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return thumbnails.stream()
                .map(thumbnail -> new ThumbnailResponse(thumbnail.getId(), thumbnail.getUrl(),thumbnail.getCreatedAt()))
                .collect(Collectors.toList());
    }
}