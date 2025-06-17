package com.zolp.custhumb.domain.thumbnail.api;

import com.zolp.custhumb.domain.thumbnail.application.ThumbnailService;
import com.zolp.custhumb.domain.thumbnail.dto.request.CreateThumbnailRequest;
import com.zolp.custhumb.domain.thumbnail.dto.request.EditThumbnailRequest;
import com.zolp.custhumb.domain.thumbnail.dto.response.ThumbnailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/thumbnails")
@SecurityRequirement(name = "accessToken")
@Tag(name = "썸네일 API", description = "썸네일 생성 및 편집 API")
public class ThumbnailApi {
    private final ThumbnailService thumbnailService;

    @PostMapping
    @Operation(summary = "썸네일 생성", description = " 클라우드 스토리지에 저장된 비디오URL과 프롬프트를 기반으로 썸네일을 생성합니다.")
    public ResponseEntity<ThumbnailResponse> createThumbnail(@Parameter(hidden = true, description = "인증된 사용자 ID") @AuthenticationPrincipal Long userId, @RequestBody CreateThumbnailRequest createThumbnailRequest) {
        ThumbnailResponse response = thumbnailService.create(userId, createThumbnailRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/edit")
    @Operation(summary = "썸네일 편집", description = "기존 썸네일 URL과 프롬프트를 사용해 새로운 썸네일을 생성합니다.")
    public ResponseEntity<ThumbnailResponse> editThumbnail(
            @Parameter(hidden = true, description = "인증된 사용자 ID") @AuthenticationPrincipal Long userId,
            @RequestBody EditThumbnailRequest request) {
        ThumbnailResponse response = thumbnailService.edit(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "썸네일 목록 조회", description = "인증된 사용자가 생성한 모든 썸네일 목록을 조회합니다.")
    public ResponseEntity<List<ThumbnailResponse>> getThumbnails(
            @Parameter(hidden = true, description = "인증된 사용자 ID") @AuthenticationPrincipal Long userId) {
        List<ThumbnailResponse> response = thumbnailService.getThumbnails(userId);
        return ResponseEntity.ok(response);
    }
}
