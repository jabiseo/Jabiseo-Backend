package com.jabiseo.domain.problem.repository;

import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.repository.querydsl.ProblemRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepository extends ProblemRepositoryCustom, JpaRepository<Problem, Long> {

    @Query("SELECT p FROM Problem p JOIN FETCH p.certificate WHERE p.id IN :solvedProblemIds")
    List<Problem> findAllByIdWithCertificate(List<Long> solvedProblemIds);

}
