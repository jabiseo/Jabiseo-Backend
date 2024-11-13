package com.jabiseo.api.auth.application.usecase;

import com.jabiseo.domain.auth.domain.Auth;
import com.jabiseo.domain.auth.domain.AuthService;
import com.jabiseo.infra.cache.RedisCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutUseCase {

    private final AuthService authService;

    public void execute(Long memberId, String deviceId) {
        authService.logout(Auth.create(deviceId, memberId, null));
    }

}
