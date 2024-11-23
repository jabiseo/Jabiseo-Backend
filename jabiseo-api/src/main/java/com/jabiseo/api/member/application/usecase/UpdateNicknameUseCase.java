package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.request.UpdateNicknameRequest;
import com.jabiseo.api.member.dto.response.UpdateNicknameResponse;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNicknameUseCase {

    private final MemberService memberService;

    public UpdateNicknameResponse execute(Long memberId, UpdateNicknameRequest request) {
        Member member = memberService.updateNickname(memberId, request.nickname());
        return UpdateNicknameResponse.of(member);
    }
}
