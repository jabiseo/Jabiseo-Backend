package com.jabiseo.domain.learning.domain;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.problem.domain.Problem;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemSolving {

    @Id
    @Tsid
    @Column(name = "problem_solving_id")
    private Long id;

    private int selectedChoice;

    private boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Learning learning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member member;

    private ProblemSolving(Member member, Problem problem, Learning learning, int selectedChoice, boolean isCorrect) {
        this.member = member;
        this.problem = problem;
        this.learning = learning;
        this.selectedChoice = selectedChoice;
        this.isCorrect = isCorrect;
    }

    public static ProblemSolving of(Member member, Problem problem, Learning learning, int selectedChoice, boolean isCorrect) {
        ProblemSolving problemSolving = new ProblemSolving(member, problem, learning, selectedChoice, isCorrect);
        learning.addProblemSolving(problemSolving);
        return problemSolving;
    }
}
