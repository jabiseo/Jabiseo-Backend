package com.jabiseo.domain.member.repository;

import com.jabiseo.domain.member.domain.DeviceToken;
import com.jabiseo.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    Optional<DeviceToken> findByMemberAndDeviceId(Member member, String deviceId);

    void deleteByMemberAndDeviceId(Member member, String deviceId);
}
