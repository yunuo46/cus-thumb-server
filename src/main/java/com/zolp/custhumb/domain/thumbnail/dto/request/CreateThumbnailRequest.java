package com.zolp.custhumb.domain.thumbnail.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "썸네일 생성 요청 DTO")
public record CreateThumbnailRequest(
        @Schema(description = "생성 시 참고할 이미지 타입")
        @NotBlank
        String type
) {}
