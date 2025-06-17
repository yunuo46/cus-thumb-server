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
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

    private final ThumbnailRepository thumbnailRepository;
    private final UserRepository userRepository;
    private final GcsSignedUrlService gcsSignedUrlService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String BUCKET_NAME = "custhumb-bucket";

    public ThumbnailResponse create(Long userId, CreateThumbnailRequest createThumbnailRequest) {
        User user = userRepository.findById(userId).orElseThrow();

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String thumbnailObject = "thumbnail/" + ".png";

        URL thumbnailUploadUrl = gcsSignedUrlService.generateUploadUrl(BUCKET_NAME, thumbnailObject, "image/png", 15, userId, timestamp);

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

    public ThumbnailResponse edit(Long id, EditThumbnailRequest request) {
        Thumbnail thumbnail = thumbnailRepository.findById(id).orElseThrow();
        String newURL = "https://new-dummy.thumbnail.jpg";
        thumbnail.updateUrl(newURL);
        return new ThumbnailResponse(thumbnail.getId(), thumbnail.getUrl());
    }
}