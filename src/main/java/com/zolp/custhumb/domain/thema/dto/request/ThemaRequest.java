package com.zolp.custhumb.domain.thema.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "사용자 테마 생성 요청 DTO")
public record ThemaRequest(
        @Schema(description = "테마에 대한 프롬프트")
        @NotNull
        String prompt,

        @Schema(description = "참고 이미지의 GCS URL")
        @NotNull
        String imageUrl
) {}