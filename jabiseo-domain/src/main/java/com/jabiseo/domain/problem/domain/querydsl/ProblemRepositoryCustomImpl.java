package com.jabiseo.domain.problem.domain.querydsl;

import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryDto;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.jabiseo.domain.problem.domain.QBookmark.bookmark;
import static com.jabiseo.domain.problem.domain.QProblem.problem;
import static com.jabiseo.domain.problem.domain.QProblemInfo.problemInfo;

@RequiredArgsConstructor
public class ProblemRepositoryCustomImpl implements ProblemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProblemWithBookmarkDetailQueryDto> findDetailByExamIdAndSubjectIdWithBookmark(Long memberId, Long examId, Long subjectId, int count) {
        return makeProblemWithBookmarkDetailQuery(memberId)
                .where(examIdEq(examId), subjectIdEq(subjectId))
                .limit(count)
                .fetch();
    }

    @Override
    public List<ProblemWithBookmarkDetailQueryDto> findDetailBySubjectIdWithBookmark(Long memberId, Long subjectId) {
        return makeProblemWithBookmarkDetailQuery(memberId)
                .where(subjectIdEq(subjectId))
                .fetch();
    }

    @Override
    public List<ProblemWithBookmarkDetailQueryDto> findDetailByIdsInWithBookmark(Long memberId, List<Long> problemIds) {
        return makeProblemWithBookmarkDetailQuery(memberId)
                .where(problem.id.in(problemIds))
                .fetch();
    }

    @Override
    public Page<ProblemWithBookmarkSummaryQueryDto> findBookmarkedSummaryByExamIdAndSubjectIdsInWithBookmark(Long memberId, Long examId, List<Long> subjectIds, Pageable pageable) {
        List<ProblemWithBookmarkSummaryQueryDto> content = makeProblemWithBookmarkSummaryQuery(memberId)
                .where(memberIdEq(memberId), examIdEq(examId), subjectIdsIn(subjectIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(problem.count())
                .from(problem)
                .join(problem.problemInfo, problemInfo)
                .leftJoin(bookmark).on(bookmark.problem.id.eq(problem.id).and(bookmark.member.id.eq(memberId)))
                .where(memberIdEq(memberId), examIdEq(examId), subjectIdsIn(subjectIds));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public ProblemWithBookmarkDetailQueryDto findDetailByIdWithBookmark(Long memberId, Long problemId) {
        return makeProblemWithBookmarkDetailQuery(memberId)
                .where(problem.id.eq(problemId))
                .fetchOne();
    }

    @Override
    public List<ProblemWithBookmarkSummaryQueryDto> findSummaryByIdsInWithBookmark(Long memberId, List<Long> problemIds) {
        return makeProblemWithBookmarkSummaryQuery(memberId)
                .where(problem.id.in(problemIds))
                .fetch();
    }

    private JPAQuery<ProblemWithBookmarkDetailQueryDto> makeProblemWithBookmarkDetailQuery(Long memberId) {
        JPAQuery<ProblemWithBookmarkDetailQueryDto> query = queryFactory
                .select(
                        Projections.constructor(
                                ProblemWithBookmarkDetailQueryDto.class,
                                problem.id.as("problemId"),
                                problem.description,
                                problem.choice1,
                                problem.choice2,
                                problem.choice3,
                                problem.choice4,
                                problem.answerNumber,
                                problem.solution,
                                getIsBookmark(memberId),
                                problemInfo.examId.as("examId"),
                                problemInfo.examDescription.as("examDescription"),
                                problemInfo.subjectId.as("subjectId"),
                                problemInfo.subjectName.as("subjectName"),
                                problemInfo.subjectSequence.as("subjectSequence")
                        )
                )
                .from(problem)
                .join(problem.problemInfo, problemInfo);
        if (memberId != null) {
            return query.leftJoin(bookmark).on(bookmark.problem.id.eq(problem.id).and(bookmark.member.id.eq(memberId)));
        }
        return query;
    }

    private JPAQuery<ProblemWithBookmarkSummaryQueryDto> makeProblemWithBookmarkSummaryQuery(Long memberId) {
        JPAQuery<ProblemWithBookmarkSummaryQueryDto> query = queryFactory
                .select(
                        Projections.constructor(
                                ProblemWithBookmarkSummaryQueryDto.class,
                                problem.id.as("problemId"),
                                problem.description,
                                getIsBookmark(memberId),
                                problemInfo.examId.as("examId"),
                                problemInfo.examDescription.as("examDescription"),
                                problemInfo.subjectId.as("subjectId"),
                                problemInfo.subjectName.as("subjectName"),
                                problemInfo.subjectSequence.as("subjectSequence")
                        )
                )
                .from(problem)
                .join(problem.problemInfo, problemInfo);
        if (memberId != null) {
            return query.leftJoin(bookmark).on(bookmark.problem.id.eq(problem.id).and(bookmark.member.id.eq(memberId)));
        }
        return query;
    }

    private static BooleanExpression getIsBookmark(Long memberId) {
        return memberId == null ? Expressions.FALSE : bookmark.id.isNotNull().as("isBookmark");
    }

    private Predicate subjectIdsIn(List<Long> subjectIds) {
        return subjectIds != null ? problemInfo.subjectId.in(subjectIds) : null;
    }

    private static BooleanExpression subjectIdEq(Long subjectId) {
        return subjectId != null ? problemInfo.subjectId.eq(subjectId) : null;
    }

    private BooleanExpression examIdEq(Long examId) {
        return examId != null ? problemInfo.examId.eq(examId) : null;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return memberId != null ? bookmark.member.id.eq(memberId) : null;
    }
}
