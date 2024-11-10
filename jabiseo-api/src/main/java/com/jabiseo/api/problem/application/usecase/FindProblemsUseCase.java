package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.CertificateResponse;
import com.jabiseo.api.problem.dto.FindProblemsResponse;
import com.jabiseo.api.problem.dto.ProblemsDetailResponse;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.service.CertificateService;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;
import com.jabiseo.domain.problem.service.ProblemService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindProblemsUseCase {

    private final CertificateService certificateService;
    private final ProblemService problemService;

    //memberId가 null일 경우 비회원이므로 bookmark 유무가 모두 false로 응답된다.
    //examId가 null일 경우 전체 시험을 대상으로 조회한다.
    public FindProblemsResponse execute(@Nullable Long memberId, Long certificateId,
                                        @Nullable Long examId, List<Long> subjectIds, int count) {

        Certificate certificate = certificateService.getByIdWithExams(certificateId);
        certificateService.validateExamIdAndSubjectIds(certificate, examId, subjectIds);

        List<ProblemWithBookmarkDetailQueryDto> dtos =
                problemService.findProblemsByExamIdAndSubjectIds(memberId, examId, subjectIds, count);

        CertificateResponse certificateResponse = CertificateResponse.from(certificate);
        List<ProblemsDetailResponse> problemsDetailResponses = dtos.stream()
                .map(ProblemsDetailResponse::from)
                .toList();

        return FindProblemsResponse.of(certificateResponse, problemsDetailResponses);
    }
}
