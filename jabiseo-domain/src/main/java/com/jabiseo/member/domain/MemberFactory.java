package com.jabiseo.member.domain;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberFactory {

    private final RandomNicknameGenerator randomNicknameGenerator;

    // TODO: S3 + CDN 생성 후 변경해야 한다.
    private final String DEFAULT_IMAGE_URL = "https://github.com/Jabiseo/Jabiseo-Backend/assets/28949213/fb6cb510-05fa-4791-a9c1-74a379294936";

    public Member createNew(String oauthId, String oauthServer) {
        String nickname = randomNicknameGenerator.generate();

        // TODO: ID 생성 전략을 통해 따로 생성해야 한다.
        String id = UUID.randomUUID().toString();
        return Member.of(id, "", nickname, oauthId, oauthServer, DEFAULT_IMAGE_URL);
    }
}
