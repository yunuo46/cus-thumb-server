package com.zolp.custhumb.domain.upload.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
@Tag(name = "업로드 API", description = "GCP Presigned URL 발급 API")
public class UploadApi {
    //private final VideoService videoService;

    @GetMapping("/video")
    public ResponseEntity<String> getVideoPresignedUrl() {
        String url = "";
        return ResponseEntity.ok(url);
    }

    @GetMapping("/image")
    public ResponseEntity<String> getImagePresignedUrl() {
        String url = "";
        return ResponseEntity.ok(url);
    }
}
