package com.jabiseo.infra.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabiseo.infra.client.oidc.GoogleOpenIdConfiguration;
import com.jabiseo.infra.client.oidc.OidcPublicKey;
import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisCacheRepository {

    private final RedisTemplate<String, String> redisStringTemplate;
    private final ValueOperations<String, String> operation;
    private final ObjectMapper mapper = new ObjectMapper();

    public RedisCacheRepository(RedisTemplate<String, String> redisStringTemplate) {
        this.redisStringTemplate = redisStringTemplate;
        this.operation = redisStringTemplate.opsForValue();
    }

    public void savePublicKey(String key, List<OidcPublicKey> publicKeys) {
        try {
            String publicKeyString = mapper.writeValueAsString(publicKeys);
            // TODO: timeout 값 논의 필요
            operation.set(key, publicKeyString, 1, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<OidcPublicKey> getPublicKeys(String key) {
        String values = operation.get(key);
        if (values == null) {
            return null;
        }
        try {
            return Arrays.asList(mapper.readValue(values, OidcPublicKey[].class));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void saveOpenConfiguration(String key, GoogleOpenIdConfiguration configuration) {
        try {
            String configDocs = mapper.writeValueAsString(configuration);
            operation.set(key, configDocs, 1, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public GoogleOpenIdConfiguration getGoogleConfiguration(String key) {
        String value = operation.get(key);
        if (value == null) {
            return null;
        }

        try {
            return mapper.readValue(value, GoogleOpenIdConfiguration.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
