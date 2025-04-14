package com.zolp.custhumb.infra.domain.oauth.google;

import com.zolp.custhumb.infra.domain.oauth.OauthApiClient;
import com.zolp.custhumb.infra.domain.oauth.OauthInfoResponse;
import com.zolp.custhumb.infra.domain.oauth.OauthLoginParams;
import com.zolp.custhumb.infra.domain.oauth.OauthProvider;
import org.springframework.beans.factory.annotation.Value;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GoogleApiClient implements OauthApiClient {

    private static final String GRANT_TYPE = "authorization_code";

    @Value("${oauth.google.url.auth}")
    private String authUrl;

    @Value("${oauth.google.url.api}")
    private String apiUrl;

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect-url}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    @Override
    public OauthProvider oauthProvider() {
        return OauthProvider.GOOGLE;
    }

    @Override
    public String requestAccessToken(OauthLoginParams params) {
        String url = authUrl;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", GRANT_TYPE);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);
        GoogleTokens response = restTemplate.postForObject(url, request, GoogleTokens.class);

        System.out.println("request access token: " + Objects.requireNonNull(response).getAccessToken());

        return response.getAccessToken();
    }


    @Override
    public OauthInfoResponse requestOauthInfo(String accessToken) {
        String url = apiUrl;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        ResponseEntity<GoogleInfoResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                GoogleInfoResponse.class
        );

        return response.getBody();
    }
}
