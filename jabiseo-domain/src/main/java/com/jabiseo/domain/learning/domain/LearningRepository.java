package com.jabiseo.domain.learning.domain;

import com.jabiseo.domain.learning.domain.querydsl.LearningQueryDslRepository;
import com.jabiseo.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LearningRepository extends JpaRepository<Learning, Long>, LearningQueryDslRepository {

    @Query("select l from Learning l join fetch l.problemSolvings where l.member = :member and l.createdAt between :startDateTime and :endDateTime")
    List<Learning> findByMemberAndCreatedAtBetweenWithProblemSolvings(Member member, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
