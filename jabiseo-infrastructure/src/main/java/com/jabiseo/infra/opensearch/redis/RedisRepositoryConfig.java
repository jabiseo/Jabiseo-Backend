package com.jabiseo.infra.opensearch.redis;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = {"com.jabiseo.infra.opensearch.redis"})
public class RedisRepositoryConfig {
}
