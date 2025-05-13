package com.zolp.custhumb.domain.video.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/videos")
@Tag(name = "영상 업로드 API", description = "GCP Presigned URL 발급 API")
public class VideoApi {
    //private final VideoService videoService;

    @GetMapping("/upload-url")
    public ResponseEntity<String> getPresignedUrl() {
        String url = "";
        return ResponseEntity.ok(url);
    }
}
