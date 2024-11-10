package com.jabiseo.domain.member.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OauthMemberInfo {

    private final String oauthId;
    private final OauthServer oauthServer;
    private final String email;

    @Builder
    public OauthMemberInfo(String oauthId, OauthServer oauthServer, String email) {
        this.oauthId = oauthId;
        this.oauthServer = oauthServer;
        this.email = email;
    }
}
