package com.jabiseo.domain.learning.domain.querydsl;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.learning.dto.LearningWithSolvingCountQueryDto;
import com.jabiseo.domain.member.domain.Member;

import java.time.LocalDate;
import java.util.List;

public interface LearningQueryDslRepository {

    List<LearningWithSolvingCountQueryDto> findLearningWithSolvingCount(Member member, Certificate certificate, LocalDate startDate, LocalDate endDate);
}
