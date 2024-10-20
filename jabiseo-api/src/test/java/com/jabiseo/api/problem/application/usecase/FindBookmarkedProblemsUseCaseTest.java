package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.application.usecase.FindBookmarkedProblemsUseCase;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.member.exception.MemberBusinessException;
import com.jabiseo.domain.member.exception.MemberErrorCode;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.domain.ProblemRepository;
import com.jabiseo.api.problem.dto.FindBookmarkedProblemsResponse;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryDto;
import com.jabiseo.api.problem.dto.ProblemsSummaryResponse;
import fixture.ProblemFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static fixture.CertificateFixture.createCertificate;
import static fixture.ExamFixture.createExam;
import static fixture.MemberFixture.createMember;
import static fixture.ProblemWithBookmarkSummaryDtoFixture.createProblemWithBookmarkSummaryQueryDto;
import static fixture.SubjectFixture.createSubject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("북마크 목록 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindBookmarkedProblemsUseCaseTest {

    @InjectMocks
    FindBookmarkedProblemsUseCase sut;

    @Mock
    MemberRepository memberRepository;

    @Mock
    ProblemRepository problemRepository;

    @Test
    @DisplayName("examId가 있을 경우 북마크 목록 조회를 성공한다.")
    void givenProblemConditionsContainsExamId_whenFindingBookmarkedProblems_thenFindBookmarkedProblems() {
        //given
        Long memberId = 1L;
        Long certificateId = 2L;
        Long examId = 3L;
        Long subjectId = 4L;
        List<Long> problemIds = List.of(5L, 6L);

        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        member.updateCurrentCertificate(certificate);
        createExam(examId, certificate);
        createSubject(subjectId, certificate);
        List<Problem> problems = problemIds.stream().map(ProblemFixture::createProblem).toList();
        Pageable pageable = PageRequest.of(0, 10);
        given(memberRepository.getReferenceById(memberId)).willReturn(member);
        List<ProblemWithBookmarkSummaryQueryDto> dtos = problems.stream()
                .map(problem -> createProblemWithBookmarkSummaryQueryDto(problem, true))
                .toList();
        given(problemRepository.findBookmarkedSummaryByExamIdAndSubjectIdsInWithBookmark(memberId, examId, List.of(subjectId), pageable))
                .willReturn(new PageImpl<>(dtos, pageable, 2));

        //when
        FindBookmarkedProblemsResponse results = sut.execute(memberId, examId, List.of(subjectId), 0);

        //then
        assertThat(results.totalCount()).isEqualTo(2);
        assertThat(results.totalPage()).isEqualTo(1);
        assertThat(results.problems().get(0)).isEqualTo(ProblemsSummaryResponse.from(dtos.get(0)));
        assertThat(results.problems().get(1)).isEqualTo(ProblemsSummaryResponse.from(dtos.get(1)));
    }

    @Test
    @DisplayName("examId가 없을 경우 북마크 목록 조회를 성공한다.")
    void givenProblemConditionsExceptExamId_whenFindingBookmarkedProblems_thenFindBookmarkedProblems() {
        //given
        Long memberId = 1L;
        Long certificateId = 2L;
        Long subjectId = 4L;
        List<Long> problemIds = List.of(5L, 6L);

        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        member.updateCurrentCertificate(certificate);
        createSubject(subjectId, certificate);
        List<Problem> problems = problemIds.stream().map(ProblemFixture::createProblem).toList();
        Pageable pageable = PageRequest.of(0, 10);
        given(memberRepository.getReferenceById(memberId)).willReturn(member);
        List<ProblemWithBookmarkSummaryQueryDto> dtos = problems.stream()
                .map(problem -> createProblemWithBookmarkSummaryQueryDto(problem, true))
                .toList();
        given(problemRepository.findBookmarkedSummaryByExamIdAndSubjectIdsInWithBookmark(memberId, null, List.of(subjectId), pageable))
                .willReturn(new PageImpl<>(dtos, pageable, 2));

        //when
        FindBookmarkedProblemsResponse results = sut.execute(memberId, null, List.of(subjectId), 0);

        //then
        assertThat(results.totalCount()).isEqualTo(2);
        assertThat(results.totalPage()).isEqualTo(1);
        assertThat(results.problems().get(0)).isEqualTo(ProblemsSummaryResponse.from(dtos.get(0)));
        assertThat(results.problems().get(1)).isEqualTo(ProblemsSummaryResponse.from(dtos.get(1)));
    }

    @Test
    @DisplayName("현재 자격증이 없는 회원의 북마크 목록 조회를 시도하면 예외처리한다.")
    void givenMemberWithNoCurrentCertificate_whenFindingBookmarkedProblems_thenReturnError() {
        //given
        Long memberId = 1L;
        Long examId = 3L;
        Long subjectId = 4L;

        Member member = createMember(memberId);
        given(memberRepository.getReferenceById(memberId)).willReturn(member);

        //when & then
        assertThatThrownBy(() -> sut.execute(memberId, examId, List.of(subjectId), 0))
                .isInstanceOf(MemberBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.CURRENT_CERTIFICATE_NOT_EXIST);
    }
}
