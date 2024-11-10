package com.jabiseo.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.jabiseo.domain"})
public class JpaRepositoryConfig {
}
