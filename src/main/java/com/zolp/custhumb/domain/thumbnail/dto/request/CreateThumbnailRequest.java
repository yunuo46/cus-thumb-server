package com.zolp.custhumb.domain.thumbnail.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "썸네일 생성 요청 DTO")
public record CreateThumbnailRequest(

        @Schema(description = "썸네일 이미지 GCS URL")
        @NotNull
        String url,

        @Schema(description = "선택된 테마 ID 목록")
        @NotEmpty
        List<Long> themaIds
) {}
