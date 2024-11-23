package com.jabiseo.api.problem.controller;

import com.jabiseo.api.problem.application.usecase.*;
import com.jabiseo.api.config.auth.AuthMember;
import com.jabiseo.api.config.auth.AuthenticatedMember;
import com.jabiseo.api.problem.dto.request.CreateReportRequest;
import com.jabiseo.api.problem.dto.request.FindProblemsRequest;
import com.jabiseo.api.problem.dto.response.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems")
public class ProblemController {

    private final FindProblemsUseCase findProblemsUseCase;

    private final FindProblemsByIdUseCase findProblemsByIdUseCase;

    private final CreateReportUseCase createReportUseCase;

    private final FindBookmarkedProblemsUseCase findBookmarkedProblemsUseCase;

    private final FindProblemDetailUseCase findProblemDetailUseCase;

    private final FindSimilarProblemsUseCase findSimilarProblemsUseCase;

    private final FindVulnerableProblemsOfSubjectUseCase findVulnerableProblemsOfSubjectUseCase;

    private final FindRecommendedProblemsUseCase findRecommendedProblemsUseCase;

    private final SearchProblemUseCase searchProblemUseCase;

    @GetMapping("/set")
    public ResponseEntity<FindProblemsResponse> findProblems(
            @AuthenticatedMember AuthMember member,
            @RequestParam(name = "certificate-id") Long certificateId,
            @RequestParam(name = "exam-id", required = false) Long examId,
            @RequestParam(name = "subject-id") List<Long> subjectIds,
            @RequestParam
            @Min(value = 1, message = "과목 당 문제 수는 0보다 커야 합니다.")
            @Max(value = 20, message = "과목 당 문제 수는 20보다 작거나 같아야 합니다.")
            int count
    ) {
        FindProblemsResponse result =
                findProblemsUseCase.execute(member.getMemberId(), certificateId, examId, subjectIds, count);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/set/query")
    public ResponseEntity<FindProblemsResponse> findProblems(
            @AuthenticatedMember AuthMember member,
            @RequestBody FindProblemsRequest request
    ) {
        FindProblemsResponse result = findProblemsByIdUseCase.execute(member.getMemberId(), request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reports")
    public ResponseEntity<Void> reportProblem(
            @RequestBody CreateReportRequest request
    ) {
        String reportId = createReportUseCase.execute(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reportId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/bookmarked")
    public ResponseEntity<FindBookmarkedProblemsResponse> findBookmarkedProblems(
            @AuthenticatedMember AuthMember member,
            @RequestParam(name = "exam-id", required = false) Long examId,
            @RequestParam(name = "subject-id") List<Long> subjectIds,
            int page
    ) {
        FindBookmarkedProblemsResponse result = findBookmarkedProblemsUseCase.execute(member.getMemberId(), examId, subjectIds, page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{problem-id}")
    public ResponseEntity<FindProblemDetailResponse> findProblemDetail(
            @AuthenticatedMember AuthMember member,
            @PathVariable(name = "problem-id") Long problemId
    ) {
        FindProblemDetailResponse result = findProblemDetailUseCase.execute(member.getMemberId(), problemId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{problem-id}/similar")
    public ResponseEntity<List<FindSimilarProblemResponse>> findSimilarProblems(
            @AuthenticatedMember AuthMember member,
            @PathVariable(name = "problem-id") Long problemId
    ) {
        List<FindSimilarProblemResponse> result = findSimilarProblemsUseCase.execute(member.getMemberId(), problemId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/vulnerable-subjects/{subject-id}")
    public ResponseEntity<FindProblemsResponse> findVulnerableProblemsOfSubject(
            @AuthenticatedMember AuthMember member,
            @PathVariable(name = "subject-id") Long subjectId
    ) {
        FindProblemsResponse result = findVulnerableProblemsOfSubjectUseCase.execute(member.getMemberId(), subjectId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<FindProblemsResponse> findRecommendedProblems(
            @AuthenticatedMember AuthMember member
    ) {
        FindProblemsResponse result = findRecommendedProblemsUseCase.execute(member.getMemberId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchProblemResponse>> searchProblem(
            @AuthenticatedMember AuthMember member,
            @RequestParam(name = "certificate-id") Long certificateId,
            @RequestParam String query,
            @RequestParam(required = false, name = "last-score") Double lastScore,
            @RequestParam(required = false, name = "last-id") Long lastId
    ) {
        List<SearchProblemResponse> result = searchProblemUseCase.execute(member.getMemberId(), certificateId, query, lastScore, lastId);
        return ResponseEntity.ok(result);
    }

}
