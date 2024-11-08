package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.UpdateNicknameRequest;
import com.jabiseo.api.member.dto.UpdateNicknameResponse;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateNicknameUseCase {

    private final MemberService memberService;

    public UpdateNicknameResponse execute(Long memberId, UpdateNicknameRequest request) {
        Member member = memberService.updateNickname(memberId, request.nickname());
        return UpdateNicknameResponse.of(member);
    }
}
