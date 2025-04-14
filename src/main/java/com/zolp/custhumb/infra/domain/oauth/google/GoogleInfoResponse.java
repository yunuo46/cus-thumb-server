package com.zolp.custhumb.infra.domain.oauth.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zolp.custhumb.infra.domain.oauth.OauthInfoResponse;
import com.zolp.custhumb.infra.domain.oauth.OauthProvider;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleInfoResponse implements OauthInfoResponse {
    private String email;

    private String name;

    private String picture;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OauthProvider getOauthProvider() {
        return OauthProvider.GOOGLE;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getProfileImageUrl() {
        return picture;
    }
}
