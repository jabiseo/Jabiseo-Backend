package com.jabiseo.api.analysis.application.usecase;

import com.jabiseo.api.analysis.dto.FindTodayLearningResponse;
import com.jabiseo.domain.learning.dto.TodayLearningDto;
import com.jabiseo.domain.learning.service.LearningService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindTodayLearningUseCase {

    private final MemberService memberService;
    private final LearningService learningService;

    public FindTodayLearningResponse execute(Long memberId) {

        Member member = memberService.getByIdWithCertificate(memberId);
        member.validateCurrentCertificate();

        TodayLearningDto todayLearningDto = learningService.findTodayLearning(member);

        return FindTodayLearningResponse.from(todayLearningDto);
    }

}
