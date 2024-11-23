package com.jabiseo.domain.learning.repository.querydsl;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.learning.dto.LearningWithSolvingCountQueryDto;
import com.jabiseo.domain.member.domain.Member;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.jabiseo.domain.learning.domain.QLearning.learning;
import static com.jabiseo.domain.learning.domain.QProblemSolving.problemSolving;

@Component
@RequiredArgsConstructor
public class LearningQueryDslRepositoryImpl implements LearningQueryDslRepository {


    private final JPAQueryFactory queryFactory;

    @Override
    public List<LearningWithSolvingCountQueryDto> findLearningWithSolvingCount(Member member, Certificate certificate, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .select(Projections.constructor(
                        LearningWithSolvingCountQueryDto.class,
                        learning.mode,
                        learning.learningTime,  // TIMESTAMP를 Long으로 변환
                        learning.createdAt,
                        problemSolving.id.count().as("solvingCount") // COUNT 쿼리
                ))
                .from(learning)
                .leftJoin(problemSolving).on(learning.id.eq(problemSolving.learning.id))
                .where(
                        learning.member.id.eq(member.getId()),
                        learning.certificate.id.eq(certificate.getId()),
                        learning.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)) // 날짜 범위 조건
                )
                .groupBy(learning.id) // learning 테이블의 기본 키로 그룹화
                .fetch();
    }
}
