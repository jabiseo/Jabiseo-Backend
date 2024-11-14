package com.jabiseo.api.config;

import com.jabiseo.api.config.auth.AuthenticatedMemberArgumentResolver;
import com.jabiseo.api.config.deviceinfo.RequestDeviceInfoArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://devweb.jabiseo.com")
                .allowedMethods("POST", "GET", "DELETE", "PATCH", "OPTIONS","PUT")
                .allowCredentials(true);
    }

    @Bean
    HandlerMethodArgumentResolver authenticatedMemberArgumentResolver() {
        return new AuthenticatedMemberArgumentResolver();
    }

    @Bean
    HandlerMethodArgumentResolver requestDeviceInfoArgumentResolver() {
        return new RequestDeviceInfoArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedMemberArgumentResolver());
        resolvers.add(requestDeviceInfoArgumentResolver());
    }

}
