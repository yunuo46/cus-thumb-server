package com.zolp.custhumb.domain.thema.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 테마 응답 DTO")
public record ThemaResponse(

        @Schema(description = "테마 ID")
        Long id,

        @Schema(description = "프롬프트 설명")
        String prompt,

        @Schema(description = "참고 이미지 GCS URL")
        String imageUrl
) {}