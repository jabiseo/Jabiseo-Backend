package com.jabiseo.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EntityScan(basePackages = {"com.jabiseo.domain"})
@EnableJpaRepositories(basePackages = {"com.jabiseo.domain"})
@ConfigurationPropertiesScan
@SpringBootApplication(
        scanBasePackages = {
                "com.jabiseo.api",
                "com.jabiseo.domain",
                "com.jabiseo.infra.cache",
                "com.jabiseo.infra.client",
                "com.jabiseo.infra.opensearch",
                "com.jabiseo.infra.s3",
                "com.jabiseo.infra.kafka"
        }
)
public class JabiseoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JabiseoApiApplication.class, args);
    }
}
