package com.jabiseo.infra.kafka;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@Getter
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaCommonConfig {
    private final List<String> bootstrapServers;

    public KafkaCommonConfig(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
}
