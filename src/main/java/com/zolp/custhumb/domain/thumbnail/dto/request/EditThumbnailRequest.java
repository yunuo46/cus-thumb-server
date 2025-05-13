package com.zolp.custhumb.domain.thumbnail.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "썸네일 편집 요청 DTO")
public record EditThumbnailRequest(
        @Schema(description = "새로 편집된 썸네일 URL")
        @NotBlank
        String newUrl
) {}