package com.jabiseo.domain.problem.domain.querydsl;

import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProblemRepositoryCustom {

    List<ProblemWithBookmarkDetailQueryDto> findDetailByExamIdAndSubjectIdWithBookmark(Long memberId, Long examId, Long subjectId, int count);

    List<ProblemWithBookmarkDetailQueryDto> findDetailBySubjectIdWithBookmark(Long memberId, Long subjectId);

    List<ProblemWithBookmarkDetailQueryDto> findDetailByIdsInWithBookmark(Long memberId, List<Long> problemIds);

    ProblemWithBookmarkDetailQueryDto findDetailByIdWithBookmark(Long memberId, Long problemId);

    Page<ProblemWithBookmarkSummaryQueryDto> findBookmarkedSummaryByExamIdAndSubjectIdsInWithBookmark(Long memberId, Long examId, List<Long> subjectIds, Pageable pageable);

    List<ProblemWithBookmarkSummaryQueryDto> findSummaryByIdsInWithBookmark(Long memberId, List<Long> problemIds);
}
