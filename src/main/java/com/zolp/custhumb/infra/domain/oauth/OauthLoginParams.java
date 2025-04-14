package com.zolp.custhumb.infra.domain.oauth;

import org.springframework.util.MultiValueMap;

public interface OauthLoginParams {
    OauthProvider oauthProvider();
    MultiValueMap<String, String> makeBody();
}
