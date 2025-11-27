package com.rezero.anyotherday.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate 설정
 * AI 서버 호출을 위한 HTTP 클라이언트
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .requestFactory(this::clientHttpRequestFactory)
                .build();
    }

    /**
     * 요청/응답 로깅을 위한 BufferingClientHttpRequestFactory 설정
     */
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(30000);
        return new BufferingClientHttpRequestFactory(factory);
    }
}
