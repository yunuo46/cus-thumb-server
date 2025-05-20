package com.zolp.custhumb.domain.thema.api;

import com.zolp.custhumb.domain.thema.application.ThemaService;
import com.zolp.custhumb.domain.thema.dto.request.ThemaRequest;
import com.zolp.custhumb.domain.thema.dto.response.ThemaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/themas")
@SecurityRequirement(name = "accessToken")
@Tag(name = "테마 API", description = "사용자 테마 등록 및 조회 API")
public class ThemaApi {
    private final ThemaService themaService;

    @PostMapping
    @Operation(summary = "테마 등록", description = "프롬프트와 참고 이미지 URL을 기반으로 새로운 테마를 등록합니다.")
    public ResponseEntity<ThemaResponse> createThema(@RequestBody ThemaRequest request,
                                                     @AuthenticationPrincipal Long userId) {
        ThemaResponse response = themaService.create(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "사용자 테마 목록 조회", description = "인증된 사용자가 등록한 테마 목록을 조회합니다.")
    public ResponseEntity<List<ThemaResponse>> getUserThemas(
            @Parameter(hidden = true, description = "인증된 사용자 ID") @AuthenticationPrincipal Long userId) {

        List<ThemaResponse> themas = themaService.getUserThemas(userId);
        return ResponseEntity.ok(themas);
    }
}
