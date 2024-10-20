package com.jabiseo.api.auth.application;


import com.jabiseo.api.auth.application.oidc.OauthMemberInfo;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.RandomNicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberFactory {

    @Value("${jabiseo.default-image-url}")
    private String DEFAULT_IMAGE_URL;

    private final RandomNicknameGenerator randomNicknameGenerator;

    public Member createNew(OauthMemberInfo oauthMemberInfo) {
        String nickname = randomNicknameGenerator.generate();
        return Member.of(oauthMemberInfo.getEmail(), nickname, oauthMemberInfo.getOauthId(), oauthMemberInfo.getOauthServer(), DEFAULT_IMAGE_URL);
    }
}
