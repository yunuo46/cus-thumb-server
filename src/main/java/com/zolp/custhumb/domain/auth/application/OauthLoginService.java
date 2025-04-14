package com.zolp.custhumb.domain.auth.application;

import com.zolp.custhumb.infra.domain.oauth.OauthInfoResponse;
import com.zolp.custhumb.infra.domain.oauth.OauthLoginParams;
import com.zolp.custhumb.infra.domain.oauth.RequestOauthInfoService;
import com.zolp.custhumb.domain.auth.dto.Tokens;
import com.zolp.custhumb.domain.auth.util.OauthTokenGenerator;
import com.zolp.custhumb.domain.user.dao.UserRepository;
import com.zolp.custhumb.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthLoginService {
    private final UserRepository userRepository;
    private final OauthTokenGenerator oauthTokensGenerator;
    private final RequestOauthInfoService requestOauthInfoService;

    public Tokens login(OauthLoginParams params) {
        OauthInfoResponse oauthInfoResponse = requestOauthInfoService.request(params);
        Long memberId = findOrCreateMember(oauthInfoResponse);
        return oauthTokensGenerator.generate(memberId);
    }

    private Long findOrCreateMember(OauthInfoResponse oauthInfoResponse) {
        return userRepository.findByEmail(oauthInfoResponse.getEmail())
                .map(User::getId)
                .orElseGet(() -> newMember(oauthInfoResponse));
    }

    private Long newMember(OauthInfoResponse oauthInfoResponse) {
        User user = User.of(oauthInfoResponse.getName(),
                oauthInfoResponse.getEmail(),
                oauthInfoResponse.getOauthProvider(),
                oauthInfoResponse.getProfileImageUrl());
        return userRepository.save(user).getId();
    }
}
