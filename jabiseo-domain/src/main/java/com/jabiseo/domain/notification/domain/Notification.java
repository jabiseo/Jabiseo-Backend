package com.jabiseo.domain.notification.domain;

import com.jabiseo.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification implements Serializable {

    @Id
    @Column(name = "notification_id")
    private Long id;

    private Long memberId;

    @Enumerated(EnumType.STRING)
    private PushType pushType;

    private Long redirectId;

    private String token;

    public Notification(Long id, Long memberId, PushType pushType, Long redirectId, String token) {
        this.id = id;
        this.memberId = memberId;
        this.pushType = pushType;
        this.redirectId = redirectId;
        this.token = token;
    }


}
