package com.jabiseo.domain.learning.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Learning {

    @Id
    @Tsid
    @Column(name = "learning_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private LearningMode mode;

    private Long learningTime;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Certificate certificate;

    @OneToMany(mappedBy = "learning")
    private List<ProblemSolving> problemSolvings = new ArrayList<>();

    private Learning(LearningMode mode, Long learningTime, Member member, Certificate certificate) {
        this.mode = mode;
        this.learningTime = learningTime;
        this.member = member;
        this.certificate = certificate;
    }

    public static Learning of(LearningMode mode, Long learningTime, Member member, Certificate certificate) {
        return new Learning(mode, learningTime, member, certificate);
    }

    public void addProblemSolving(ProblemSolving problemSolving) {
        problemSolvings.add(problemSolving);
    }
}
