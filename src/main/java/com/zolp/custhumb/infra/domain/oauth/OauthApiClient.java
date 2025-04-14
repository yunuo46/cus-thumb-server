package com.zolp.custhumb.infra.domain.oauth;

public interface OauthApiClient {
    OauthProvider oauthProvider();
    String requestAccessToken(OauthLoginParams params);
    OauthInfoResponse requestOauthInfo(String accessToken);
}
