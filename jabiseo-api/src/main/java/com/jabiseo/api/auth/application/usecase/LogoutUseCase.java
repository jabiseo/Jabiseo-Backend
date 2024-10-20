package com.jabiseo.api.auth.application.usecase;

import com.jabiseo.infra.cache.RedisCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final RedisCacheRepository redisCacheRepository;

    public void execute(Long memberId) {
        redisCacheRepository.deleteToken(memberId);
    }

}
