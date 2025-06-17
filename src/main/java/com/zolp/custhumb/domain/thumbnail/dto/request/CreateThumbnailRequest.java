package com.zolp.custhumb.domain.thumbnail.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "썸네일 생성 요청 DTO")
public record CreateThumbnailRequest(
        @Schema(description = "생성 시 참고할 이미지 스타일 타입들")
        @NotEmpty(message = "스타일 타입은 최소 하나 이상 제공되어야 합니다.")
        @NotNull(message = "스타일 타입 리스트는 null일 수 없습니다.")
        @Valid
        List<String> types
) {}
