package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.service.CertificateService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.exception.MemberBusinessException;
import com.jabiseo.domain.member.exception.MemberErrorCode;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryPageDto;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@DisplayName("북마크 목록 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindBookmarkedProblemsUseCaseTest {

    @InjectMocks
    FindBookmarkedProblemsUseCase sut;

    @Mock
    MemberService memberService;

    @Mock
    ProblemService problemService;

    @Mock
    CertificateService certificateService;

    @Test
    @DisplayName("현재 자격증이 없는 경우 예외 처리한다.")
    void givenNoCurrentCertificate_whenFindingBookmarkedProblems_thenThrowException() {
        //given
        Long memberId = 1L;
        Long examId = 3L;
        Long subjectId = 4L;
        int page = 0;
        Member member = createMember(memberId);

        given(memberService.getByIdWithCertificate(memberId)).willReturn(member);

        //when & then
        assertThatThrownBy(() -> sut.execute(memberId, examId, List.of(subjectId), page))
                .isInstanceOf(MemberBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.CURRENT_CERTIFICATE_NOT_EXIST);
    }

    @Test
    @DisplayName("북마크 목록 조회를 성공한다.")
    void givenProblemConditions_whenFindingBookmarkedProblems_thenFindBookmarkedProblems() {
        //given
        Long memberId = 1L;
        Long certificateId = 2L;
        Long examId = 3L;
        Long subjectId = 4L;
        int page = 0;

        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        member.updateCurrentCertificate(certificate);

        given(memberService.getByIdWithCertificate(memberId)).willReturn(member);
        doNothing().when(certificateService).validateExamIdAndSubjectIds(certificateId, examId, List.of(subjectId));
        given(problemService.findBookmarkedProblems(memberId, examId, List.of(subjectId), page)).willReturn(
                new ProblemWithBookmarkSummaryQueryPageDto(0, 0, List.of())
        );

        //when
        sut.execute(memberId, examId, List.of(subjectId), page);

        //then
        verify(problemService).findBookmarkedProblems(memberId, examId, List.of(subjectId), page);
    }
}
