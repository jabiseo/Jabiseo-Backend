package com.jabiseo.domain.learning.domain;

import com.jabiseo.domain.learning.domain.querydsl.LearningQueryDslRepository;
import com.jabiseo.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LearningRepository extends JpaRepository<Learning, Long>, LearningQueryDslRepository {

    @Query("SELECT l FROM Learning l JOIN FETCH l.problemSolvings WHERE l.member = :member AND l.createdAt BETWEEN :startDateTime AND :endDateTime")
    List<Learning> findByMemberAndCreatedAtBetweenWithProblemSolvings(Member member, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
