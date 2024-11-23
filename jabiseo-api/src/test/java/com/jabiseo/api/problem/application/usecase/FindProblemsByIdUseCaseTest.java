package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.request.FindProblemsRequest;
import com.jabiseo.api.problem.dto.response.FindProblemsResponse;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import com.jabiseo.domain.problem.service.ProblemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static fixture.CertificateFixture.createCertificate;
import static fixture.MemberFixture.createMember;
import static fixture.ProblemFixture.createProblem;
import static fixture.ProblemWithBookmarkDetailQueryDtoFixture.createProblemWithBookmarkDetailQueryDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("문제 ID로 문제 세트 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindProblemsByIdUseCaseTest {

    @InjectMocks
    FindProblemsByIdUseCase sut;

    @Mock
    ProblemService problemService;

    @Mock
    MemberService memberService;

    @Test
    @DisplayName("북마크를 통한 문제 세트 조회 테스트를 성공한다.")
    void givenProblemIds_whenFindingProblems_thenFindProblems() {
        //given
        Long memberId = 1L;
        Long certificateId = 2L;
        List<Long> problemIds = List.of(3L, 4L, 5L);
        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        member.updateCurrentCertificate(certificate);
        List<Problem> problems = List.of(
                createProblem(problemIds.get(0), certificate),
                createProblem(problemIds.get(1), certificate),
                createProblem(problemIds.get(2), certificate)
        );
        List<ProblemWithBookmarkDetailQueryDto> problemWithBookmarkDetailQueryDtos = List.of(
                createProblemWithBookmarkDetailQueryDto(problems.get(0), true),
                createProblemWithBookmarkDetailQueryDto(problems.get(1), true),
                createProblemWithBookmarkDetailQueryDto(problems.get(2), true)
        );
        FindProblemsRequest request = new FindProblemsRequest(problemIds);

        given(memberService.getByIdWithCertificate(memberId)).willReturn(member);
        given(problemService.findProblemsById(memberId, problemIds)).willReturn(problemWithBookmarkDetailQueryDtos);

        //when
        FindProblemsResponse result = sut.execute(member.getId(), request);

        //then
        assertThat(result.certificateInfo().certificateId()).isEqualTo(certificateId);
        assertThat(result.problems().get(0).problemId()).isEqualTo(problemIds.get(0));
        assertThat(result.problems().get(1).problemId()).isEqualTo(problemIds.get(1));
        assertThat(result.problems().get(2).problemId()).isEqualTo(problemIds.get(2));
    }

    @Test
    @DisplayName("문제 세트 조회 요청 시 문제 ID가 중복되면 중복 제거 후 결과를 반환한다.")
    void givenDuplicatedProblemIds_whenFindingProblems_thenRemoveDuplicatedProblemIds() {
        //given
        Long memberId = 1L;
        Long certificateId = 2L;
        List<Long> problemIds = List.of(3L, 4L, 5L);
        List<Long> requestProblemIds = List.of(3L, 4L, 5L, 5L, 4L);
        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        member.updateCurrentCertificate(certificate);
        List<Problem> problems = List.of(
                createProblem(problemIds.get(0), certificate),
                createProblem(problemIds.get(1), certificate),
                createProblem(problemIds.get(2), certificate)
        );
        List<ProblemWithBookmarkDetailQueryDto> problemWithBookmarkDetailQueryDtos = List.of(
                createProblemWithBookmarkDetailQueryDto(problems.get(0), true),
                createProblemWithBookmarkDetailQueryDto(problems.get(1), true),
                createProblemWithBookmarkDetailQueryDto(problems.get(2), true)
        );
        FindProblemsRequest request = new FindProblemsRequest(requestProblemIds);

        given(memberService.getByIdWithCertificate(memberId)).willReturn(member);
        given(problemService.findProblemsById(memberId, problemIds)).willReturn(problemWithBookmarkDetailQueryDtos);

        //when
        FindProblemsResponse result = sut.execute(member.getId(), request);

        //then
        assertThat(result.certificateInfo().certificateId()).isEqualTo(certificateId);
        assertThat(result.problems().get(0).problemId()).isEqualTo(problemIds.get(0));
        assertThat(result.problems().get(1).problemId()).isEqualTo(problemIds.get(1));
        assertThat(result.problems().get(2).problemId()).isEqualTo(problemIds.get(2));
    }

    @Test
    @DisplayName("존재하지 않는 문제로 북마크를 통한 문제 세트 조회를 하면 예외처리한다.")
    void givenNonExistedProblemIds_whenFindingProblems_thenReturnError() {
        //given
        Long memberId = 1L;
        Long certificateId = 2L;
        List<Long> problemIds = List.of(3L, 4L, 5L);
        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        member.updateCurrentCertificate(certificate);
        problemIds.forEach(problemId -> createProblem(problemId, certificate));
        FindProblemsRequest request = new FindProblemsRequest(problemIds);

        given(memberService.getByIdWithCertificate(memberId)).willReturn(member);
        given(problemService.findProblemsById(memberId, problemIds)).willReturn(List.of());

        //when & then
        assertThatThrownBy(() -> sut.execute(member.getId(), request))
                .isInstanceOf(ProblemBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProblemErrorCode.PROBLEM_NOT_FOUND);
    }
}
