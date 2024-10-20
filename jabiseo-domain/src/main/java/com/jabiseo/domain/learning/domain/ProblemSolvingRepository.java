package com.jabiseo.domain.learning.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProblemSolvingRepository extends JpaRepository<ProblemSolving, Long> {

    @Query("SELECT ps " +
           "FROM ProblemSolving ps " +
           "JOIN FETCH ps.learning l " +
           "WHERE ps.member = :member AND l.certificate = :certificate AND l.createdAt > :fromDate " +
           "ORDER BY l.createdAt DESC")
    List<ProblemSolving> findWithLearningByCreatedAtAfterOrderByCreatedAtDesc(
            @Param("member") Member member,
            @Param("certificate") Certificate certificate,
            @Param("fromDate") LocalDateTime fromDate,
            Pageable pageable
    );

}
