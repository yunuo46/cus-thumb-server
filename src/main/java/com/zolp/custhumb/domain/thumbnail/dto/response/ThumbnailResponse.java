package com.zolp.custhumb.domain.thumbnail.dto.response;

import com.google.type.DateTime;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "썸네일 응답 DTO")
public record ThumbnailResponse(

        @Schema(description = "썸네일 ID")
        Long id,

        @Schema(description = "썸네일 이미지 URL")
        String url,

        @Schema(description = "썸네일 생성 시간")
        LocalDateTime createdAt
) {}
