package com.zolp.custhumb.domain.auth.application;

import com.zolp.custhumb.domain.auth.dao.RefreshTokenRepository;
import com.zolp.custhumb.domain.auth.domain.RefreshToken;
import com.zolp.custhumb.domain.auth.dto.Tokens;
import com.zolp.custhumb.domain.auth.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public Tokens reissue(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        RefreshToken tokenInDb = refreshTokenRepository.findByValue(refreshToken)
                .orElseThrow(() -> new RuntimeException("DB에 존재하지 않는 Refresh Token입니다."));

        Authentication authentication = tokenProvider.getAuthentication(tokenInDb.getKey());
        Tokens newTokens = tokenProvider.generateTokenDto(authentication);

        tokenInDb.updateValue(newTokens.getRefreshToken());
        refreshTokenRepository.save(tokenInDb);

        return newTokens;
    }
}