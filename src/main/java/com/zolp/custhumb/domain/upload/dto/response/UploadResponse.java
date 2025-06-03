package com.zolp.custhumb.domain.upload.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "업로드 응답 DTO")
public record UploadResponse(Long userId, String url) {
    
}
