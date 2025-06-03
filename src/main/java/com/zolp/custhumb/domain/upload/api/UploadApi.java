package com.zolp.custhumb.domain.upload.api;

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

    private final GcsSignedUrlService gcsSignedUrlService;

    private static final String BUCKET_NAME = "custhumb-bucket"; // 🔁 실제 GCS 버킷 이름으로 교체

    @GetMapping("/video")
    public ResponseEntity<UploadResponse> getVideoPresignedUrl(@AuthenticationPrincipal Long userId, @RequestParam String fileName) {
        URL url = gcsSignedUrlService.generateUploadUrl(BUCKET_NAME, fileName, "video/mp4", 15);
        return ResponseEntity.ok(new UploadResponse(userId, url.toString()));
    }

    @GetMapping("/image")
    public ResponseEntity<UploadResponse> getImagePresignedUrl(@AuthenticationPrincipal Long userId, @RequestParam String fileName) {
        URL url = gcsSignedUrlService.generateUploadUrl(BUCKET_NAME, fileName, "image/png", 5);
        return ResponseEntity.ok(new UploadResponse(userId, url.toString()));
    }
}
