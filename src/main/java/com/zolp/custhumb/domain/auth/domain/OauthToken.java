package com.zolp.custhumb.domain.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OauthToken {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;

    public static OauthToken of(String accessToken, String refreshToken, String grantType, Long expiresIn) {
        return new OauthToken(accessToken, refreshToken, grantType, expiresIn);
    }
}