package com.zolp.custhumb.domain.upload.application;

import com.zolp.custhumb.domain.upload.dto.response.UploadResponse;
import com.zolp.custhumb.infra.domain.gcs.GcsSignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final GcsSignedUrlService gcsSignedUrlService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public UploadResponse generateVideoAndTextUrls(Long userId, String fileName, String bucket) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String baseName = getBaseName(fileName);

        String videoObject = "video/" + baseName + ".mp4";
        String textObject = "video/" + baseName + ".txt";

        URL videoUrl = gcsSignedUrlService.generateUploadUrl(bucket, videoObject, "video/mp4", 15, userId, timestamp);
        URL textUrl = gcsSignedUrlService.generateUploadUrl(bucket, textObject, "text/plain", 5, userId, timestamp);

        return new UploadResponse(videoUrl.toString(), textUrl.toString());
    }

    public UploadResponse generateImageAndTextUrls(Long userId, String fileName, String bucket) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String baseName = getBaseName(fileName);

        String imageObject = "image/" + baseName + ".png";
        String textObject = "image/" + baseName + ".txt";

        URL imageUrl = gcsSignedUrlService.generateUploadUrl(bucket, imageObject, "image/png", 5, userId, timestamp);
        URL textUrl = gcsSignedUrlService.generateUploadUrl(bucket, textObject, "text/plain", 5, userId, timestamp);

        return new UploadResponse(imageUrl.toString(), textUrl.toString());
    }

    private String getBaseName(String fileName) {
        return fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
    }
}
