package com.zolp.custhumb.domain.user.domain;

import com.zolp.custhumb.infra.domain.oauth.OauthProvider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    private String profileImageUrl;

    public static User of(String name, String email, OauthProvider provider, String profileImageUrl) {
        return User.builder()
                .name(name)
                .email(email)
                .oauthProvider(provider)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
