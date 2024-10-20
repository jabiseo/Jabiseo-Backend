package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.FindMyInfoResponse;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindMyInfoUseCase {

    private final MemberRepository memberRepository;

    public FindMyInfoResponse execute(Long id) {
        Member member = memberRepository.getReferenceById(id);
        return FindMyInfoResponse.from(member);
    }
}
