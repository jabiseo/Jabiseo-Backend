package com.jabiseo.domain.member.service;

import com.jabiseo.domain.member.domain.DeviceToken;
import com.jabiseo.domain.member.repository.DeviceTokenRepository;
import com.jabiseo.domain.member.domain.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    public void loginToken(Member member, String token, String deviceId) {
        if (token == null) {
            return;
        }

        DeviceToken deviceToken = deviceTokenRepository.findByMemberAndDeviceId(member, deviceId)
                .orElseGet(() -> {
                    DeviceToken newToken = DeviceToken.create(deviceId, token, member);
                    return deviceTokenRepository.save(newToken);
                });

        deviceToken.updateToken(token);
    }

    public void deleteToken(Member member, String deviceId) {
        deviceTokenRepository.deleteByMemberAndDeviceId(member, deviceId);
    }
}
