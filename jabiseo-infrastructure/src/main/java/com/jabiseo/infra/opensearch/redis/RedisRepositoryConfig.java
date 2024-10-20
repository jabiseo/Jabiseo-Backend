package com.jabiseo.infra.opensearch.redis;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories(basePackages = {"com.jabiseo.infra.opensearch.redis"})
@Configuration
public class RedisRepositoryConfig {
}
