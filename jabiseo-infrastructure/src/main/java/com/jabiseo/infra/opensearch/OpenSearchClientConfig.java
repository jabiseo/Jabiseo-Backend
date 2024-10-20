package com.jabiseo.infra.opensearch;

import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5Transport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;

@Configuration
@Slf4j
public class OpenSearchClientConfig {

    @Value("${opensearch.url}")
    private String openSearchUrl;

    @Value("${opensearch.username}")
    private String openSearchUsername;

    @Value("${opensearch.password}")
    private String openSearchPassword;

    @Bean
    public OpenSearchClient openSearchClient() {

        HttpHost host;
        try {
             host = HttpHost.create(openSearchUrl);
        } catch (URISyntaxException e) {
            log.error("OpenSearchClientConfig openSearchClient error: {}", e.getMessage());
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        ApacheHttpClient5Transport transport = ApacheHttpClient5TransportBuilder.builder(host)
                .setMapper(new JacksonJsonpMapper())
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(new AuthScope(host), new UsernamePasswordCredentials(openSearchUsername, openSearchPassword.toCharArray()));
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                })
                .build();
        return new OpenSearchClient(transport);
    }
}
