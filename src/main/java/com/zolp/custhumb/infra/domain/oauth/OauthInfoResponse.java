package com.zolp.custhumb.infra.domain.oauth;

public interface OauthInfoResponse {
    String getEmail();
    String getName();
    OauthProvider getOauthProvider();
    String getProfileImageUrl();
}
