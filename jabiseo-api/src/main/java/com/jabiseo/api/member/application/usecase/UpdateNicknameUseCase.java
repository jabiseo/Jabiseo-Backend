package com.jabiseo.api.member.application.usecase;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.api.member.dto.UpdateNicknameRequest;
import com.jabiseo.api.member.dto.UpdateNicknameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNicknameUseCase {

    private final MemberRepository memberRepository;

    public UpdateNicknameResponse execute(Long memberId, UpdateNicknameRequest request) {
        Member member = memberRepository.getReferenceById(memberId);
        member.updateNickname(request.nickname());
        return UpdateNicknameResponse.of(member);
    }
}
