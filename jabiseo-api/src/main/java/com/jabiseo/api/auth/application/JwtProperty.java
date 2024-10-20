package com.jabiseo.api.auth.application;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    private final String accessKey;
    private final String refreshKey;
    private final Integer accessExpiredMin;
    private final Integer refreshExpiredDay;

    public JwtProperty(String accessKey, String refreshKey, Integer accessExpiredMin, Integer refreshExpiredDay) {
        this.accessKey = accessKey;
        this.refreshKey = refreshKey;
        this.accessExpiredMin = accessExpiredMin;
        this.refreshExpiredDay = refreshExpiredDay;
    }
}
