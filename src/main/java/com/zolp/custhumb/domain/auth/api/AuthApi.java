package com.zolp.custhumb.domain.auth.api;

import com.zolp.custhumb.domain.auth.application.OauthLoginService;
import com.zolp.custhumb.domain.auth.application.TokenService;
import com.zolp.custhumb.domain.auth.dto.Tokens;
import com.zolp.custhumb.domain.auth.dto.response.TokenResponse;
import com.zolp.custhumb.domain.auth.util.JwtUtil;
import com.zolp.custhumb.infra.domain.oauth.google.GoogleLoginParams;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApi {

    private final OauthLoginService oauthLoginService;
    private final TokenService tokenService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response) {
        Tokens newTokens = tokenService.reissue(refreshToken);

        TokenResponse tokenResponse = JwtUtil.setJwtResponse(response, newTokens);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginGoogle(@RequestBody GoogleLoginParams params, HttpServletResponse response) {
        System.out.println("loginGoogle api call");
        Tokens tokens = oauthLoginService.login(params);
        System.out.println("login google finished");
        TokenResponse tokenResponseDto = JwtUtil.setJwtResponse(response, tokens);
        return ResponseEntity.ok(tokenResponseDto);
    }
}
