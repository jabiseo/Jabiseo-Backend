package com.jabiseo.domain.problem.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByMemberIdAndProblemId(Long memberId, Long problemId);

    Optional<Bookmark> findByMemberIdAndProblemId(Long memberId, Long problemId);

}
