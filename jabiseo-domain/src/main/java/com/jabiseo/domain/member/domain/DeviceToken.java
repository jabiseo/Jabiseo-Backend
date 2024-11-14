package com.jabiseo.domain.member.domain;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken {
    @Id
    @Tsid
    @Column(name = "device_token_id")
    private Long id;

    private String deviceId;

    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public DeviceToken(String deviceId, String token, Member member) {
        this.deviceId = deviceId;
        this.token = token;
        this.member = member;
    }

    public static DeviceToken create(String deviceId, String token, Member member) {
        return new DeviceToken(deviceId, token, member);
    }

    public void updateToken(String token){
        this.token = token;
    }


}
