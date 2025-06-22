package com.zolp.custhumb.domain.upload.api;

import com.zolp.custhumb.domain.upload.application.UploadService;
import com.zolp.custhumb.domain.upload.dto.response.UploadResponse;
import com.zolp.custhumb.infra.domain.gcs.GcsSignedUrlService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
@SecurityRequirement(name = "accessToken")
@Tag(name = "업로드 API", description = "GCP Presigned URL 발급 API")
public class UploadApi {

    private final UploadService uploadService;

    private static final String BUCKET_NAME = "custhumb-bucket";

    @GetMapping("/video")
    public ResponseEntity<UploadResponse> getVideoPresignedUrl(@AuthenticationPrincipal Long userId, @RequestParam String fileName) {
        UploadResponse response = uploadService.generateVideoAndTextUrls(userId, fileName, BUCKET_NAME);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/image")
    public ResponseEntity<UploadResponse> getImagePresignedUrl(@AuthenticationPrincipal Long userId, @RequestParam String fileName) {
        UploadResponse response = uploadService.generateImageAndTextUrls(userId, fileName, BUCKET_NAME);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/masking")
    public ResponseEntity<String> getMaskingPresignedUrl(@AuthenticationPrincipal Long userId, @RequestParam String fileName) {
        String response = uploadService.generateMaskingUrl(userId, fileName, BUCKET_NAME);
        return ResponseEntity.ok(response);
    }
}
