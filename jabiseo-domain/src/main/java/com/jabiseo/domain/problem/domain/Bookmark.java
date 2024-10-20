package com.jabiseo.domain.problem.domain;

import com.jabiseo.domain.member.domain.Member;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Bookmark {

    @Id
    @Tsid
    @Column(name = "bookmark_id")
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Problem problem;

    private Bookmark(Member member, Problem problem) {
        this.member = member;
        this.problem = problem;
    }

    public static Bookmark of(Member member, Problem problem) {
        Bookmark bookmark = new Bookmark(member, problem);
        member.addBookmark(bookmark);
        return bookmark;
    }

}
