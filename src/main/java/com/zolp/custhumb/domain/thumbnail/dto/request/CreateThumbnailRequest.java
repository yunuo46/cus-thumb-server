package com.zolp.custhumb.domain.thumbnail.dto.request;

import com.zolp.custhumb.domain.thumbnail.dto.ReferenceImage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "썸네일 생성 요청 DTO")
public record CreateThumbnailRequest(

        @Schema(description = "영상 파일의 GCS URL")
        @NotBlank
        String videoUrl,

        @Schema(description = "영상에 대한 설명 프롬프트")
        @NotBlank
        String videoPrompt,

        @Schema(description = "참조 이미지 및 설명 리스트")
        @NotEmpty
        List<ReferenceImage> referenceImages
) {}
