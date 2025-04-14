package com.zolp.custhumb.domain.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;

    public static JwtToken of(String accessToken, String refreshToken, String grantType, Long expiresIn) {
        return new JwtToken(accessToken, refreshToken, grantType, expiresIn);
    }
}
