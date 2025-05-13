package com.zolp.custhumb.domain.thumbnail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "참조 이미지와 해당 설명 프롬프트 DTO")
public record ReferenceImage(

        @Schema(description = "참조 이미지 URL")
        @NotBlank
        String imageUrl,

        @Schema(description = "해당 이미지에 대한 설명 프롬프트")
        @NotBlank
        String prompt

) {}
