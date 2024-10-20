package com.jabiseo.api.analysis.application.usecase;

import com.jabiseo.api.analysis.dto.FindTodayLearningResponse;
import com.jabiseo.domain.learning.dto.TodayLearningDto;
import com.jabiseo.domain.learning.service.LearningService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindTodayLearningUseCase {

    private final MemberRepository memberRepository;
    private final LearningService learningService;

    public FindTodayLearningResponse execute(Long memberId) {

        Member member = memberRepository.getReferenceById(memberId);
        member.validateCurrentCertificate();

        TodayLearningDto todayLearningDto = learningService.findTodayLearning(member);

        return FindTodayLearningResponse.from(todayLearningDto);
    }

}
