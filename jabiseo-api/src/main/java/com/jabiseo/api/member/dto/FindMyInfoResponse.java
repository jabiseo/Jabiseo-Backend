package com.jabiseo.api.member.dto;

import com.jabiseo.domain.member.domain.Member;

public record FindMyInfoResponse(
        String memberId,
        String nickname,
        String email,
        String profileImage
) {

    public static FindMyInfoResponse from(Member member) {
        return new FindMyInfoResponse(member.getId().toString(), member.getNickname(), member.getEmail(), member.getProfileImage());
    }

}
