package com.jabiseo.api.config.auth;

public class AuthMember {

    Long memberId;

    public AuthMember(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
