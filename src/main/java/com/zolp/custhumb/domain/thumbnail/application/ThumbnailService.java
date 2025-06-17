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
import java.time.format.DateTimeFormatter;
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

    public ThumbnailResponse create(Long userId, CreateThumbnailRequest createThumbnailRequest) {
        User user = userRepository.findById(userId).orElseThrow();

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String thumbnailObject = "thumbnail/" + ".png";

        URL thumbnailUploadUrl = gcsSignedUrlService.generateUploadUrl(gcsBucketName, thumbnailObject, "image/png", 15, userId, timestamp);

        Map<String, Object> data = gcsSignedUrlService.getLatestMediaData(String.valueOf(userId));
        String thumbnailUrl = gcsSignedUrlService.requestThumbnailToAiServer(
                (String) data.get("videoUrl"),
                (String) data.get("videoPrompt"),
                thumbnailUploadUrl.toString(),
                createThumbnailRequest.type(),
                userId
        );

        Thumbnail thumbnail = Thumbnail.builder()
                .url(thumbnailUrl)
                .user(user)
                .build();

        System.out.println(thumbnailUrl);

        Thumbnail saved = thumbnailRepository.save(thumbnail);
        return new ThumbnailResponse(saved.getId(), saved.getUrl());
    }

    public ThumbnailResponse edit(Long userId, EditThumbnailRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String thumbnailObject = "thumbnail/" + ".png";

        URL newThumbnailUploadUrl = gcsSignedUrlService.generateUploadUrl(
                gcsBucketName,
                thumbnailObject,
                "image/png",
                15,
                userId,
                timestamp
        );

        String newThumbnailUrl = gcsSignedUrlService.requestThumbnailEditToAiServer(
                request.url(),
                request.prompt(),
                newThumbnailUploadUrl.toString(),
                userId
        );

        Thumbnail editedThumbnail = Thumbnail.builder()
                .url(newThumbnailUrl)
                .user(user)
                .build();

        Thumbnail saved = thumbnailRepository.save(editedThumbnail);
        return new ThumbnailResponse(saved.getId(), saved.getUrl());
    }

    public List<ThumbnailResponse> getThumbnails(Long userId) {
        List<Thumbnail> thumbnails = thumbnailRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return thumbnails.stream()
                .map(thumbnail -> new ThumbnailResponse(thumbnail.getId(), thumbnail.getUrl()))
                .collect(Collectors.toList());
    }
}