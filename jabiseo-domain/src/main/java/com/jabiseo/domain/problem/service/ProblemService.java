package com.jabiseo.domain.problem.service;

import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.repository.ProblemRepository;
import com.jabiseo.domain.problem.dto.*;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private static final int MAX_PROBLEM_COUNT = 20;
    private static final int SIMILAR_PROBLEM_COUNT = 3;
    private static final int SEARCH_PROBLEM_COUNT = 10;
    private static final int DEFAULT_BOOKMARK_PAGE_SIZE = 10;

    private final ProblemRepository problemRepository;
    private final SimilarProblemsProvider similarProblemsProvider;
    private final ProblemSearchProvider problemSearchProvider;

    public Problem getById(Long problemId) {
        return problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemBusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND));
    }

    public ProblemWithBookmarkSummaryQueryPageDto findBookmarkedProblems(Long memberId, Long examId, List<Long> subjectIds, int page) {
        Pageable pageable = PageRequest.of(page, DEFAULT_BOOKMARK_PAGE_SIZE);
        Page<ProblemWithBookmarkSummaryQueryDto> problems = problemRepository.findBookmarkedSummaryByExamIdAndSubjectIdsInWithBookmark(memberId, examId, subjectIds, pageable);
        return ProblemWithBookmarkSummaryQueryPageDto.from(problems);
    }

    public List<ProblemWithBookmarkSummaryQueryDto> findSimilarProblems(Long memberId, Long problemId, Long certificateId) {
        List<Long> similarProblemIds = similarProblemsProvider.findSimilarProblemIds(problemId, certificateId, SIMILAR_PROBLEM_COUNT);
        return problemRepository.findSummaryByIdsInWithBookmark(memberId, similarProblemIds);
    }

    public List<ProblemWithBookmarkDetailQueryDto> findProblemsById(Long memberId, List<Long> problemIds) {
        return problemRepository.findDetailByIdsInWithBookmark(memberId, problemIds);
    }

    public List<ProblemWithBookmarkDetailQueryDto> findProblemsByExamIdAndSubjectIds(Long memberId, Long examId, List<Long> subjectIds, int count) {
        // examId가 null일 경우 전체 시험을 대상으로 조회한다.
        if (examId == null) {
            return findProblemsBySubjectId(memberId, subjectIds, count);
        }
        return subjectIds.stream()
                .distinct()
                .flatMap(subjectId -> {
                    List<ProblemWithBookmarkDetailQueryDto> problems =
                            problemRepository.findDetailByExamIdAndSubjectIdWithBookmark(memberId, examId, subjectId, MAX_PROBLEM_COUNT);
                    Collections.shuffle(problems); // 문제 리스트를 랜덤으로 섞음
                    return problems.stream().limit(count);
                })
                .toList();
    }

    public List<ProblemWithBookmarkDetailQueryDto> findProblemsBySubjectId(Long memberId, List<Long> subjectIds, int count) {
        return subjectIds.stream()
                .distinct()
                .flatMap(subjectId -> {
                    List<ProblemWithBookmarkDetailQueryDto> problems =
                            problemRepository.findDetailBySubjectIdWithBookmark(memberId, subjectId);
                    Collections.shuffle(problems); // 문제 리스트를 랜덤으로 섞음
                    return problems.stream().limit(count);
                })
                .toList();
    }

    public List<ProblemWithBookmarkSummaryScoreQueryDto> searchProblem(Long memberId, Long certificateId, String query, Double lastScore, Long lastId) {
        List<ProblemSearchDto> problemSearchDtos = problemSearchProvider.searchProblem(query, lastScore, lastId, certificateId, SEARCH_PROBLEM_COUNT);
        List<Long> problemIds = problemSearchDtos.stream()
                .map(ProblemSearchDto::problemId)
                .toList();
        List<ProblemWithBookmarkSummaryQueryDto> dtos = problemRepository.findSummaryByIdsInWithBookmark(memberId, problemIds);

        // problemId에 대응되는 ProblemWithBookmarkSummaryQueryDto를 O(1)에 찾기 위해 Map으로 변환
        Map<Long, ProblemWithBookmarkSummaryQueryDto> dtoMap = dtos.stream()
                .collect(Collectors.toMap(ProblemWithBookmarkSummaryQueryDto::problemId, dto -> dto));

        return problemSearchDtos.stream()
                .map(dto -> ProblemWithBookmarkSummaryScoreQueryDto.of(dtoMap.get(dto.problemId()), dto.score()))
                .toList();
    }

    public List<Problem> findAllByIdWithCertificate(List<Long> solvedProblemIds) {
        return problemRepository.findAllByIdWithCertificate(solvedProblemIds);
    }

    public ProblemWithBookmarkDetailQueryDto findDetailByIdWithBookmark(Long memberId, Long problemId) {
        return problemRepository.findDetailByIdWithBookmark(memberId, problemId);
    }
}
