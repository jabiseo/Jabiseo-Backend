package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.FindMyInfoResponse;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindMyInfoUseCase {

    private final MemberService memberService;

    public FindMyInfoResponse execute(Long id) {
        Member member = memberService.getById(id);
        return FindMyInfoResponse.from(member);
    }
}
