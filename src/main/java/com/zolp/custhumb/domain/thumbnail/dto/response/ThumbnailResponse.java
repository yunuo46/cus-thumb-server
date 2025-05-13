package com.zolp.custhumb.domain.thumbnail.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "썸네일 응답 DTO")
public record ThumbnailResponse(

        @Schema(description = "썸네일 ID")
        Long id,

        @Schema(description = "썸네일 이미지 URL", example = "https://storage.googleapis.com/bucket/final.jpg")
        String url
) {}
