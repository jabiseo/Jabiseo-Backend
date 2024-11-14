package com.jabiseo.infra.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabiseo.domain.auth.domain.Auth;
import com.jabiseo.domain.auth.domain.AuthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class AuthRepositoryImpl implements AuthRepository {

    private final ValueOperations<String, String> operation;

    public AuthRepositoryImpl(RedisTemplate<String, String> redisStringTemplate) {
        this.operation = redisStringTemplate.opsForValue();
    }

    @Override
    public void saveAuth(Auth auth) {
        operation.set(auth.getAuthKey(), auth.getTokenValue());
    }

    @Override
    public Optional<Auth> getSavedAuth(Auth auth) {
        return Optional.ofNullable(operation.get(auth.getAuthKey()))
                .map(token -> Auth.create(auth.getDeviceId(), auth.getMemberId(), token));
    }

    @Override
    public void deleteAuth(String authKey) {
        operation.getAndDelete(authKey);
    }
}
