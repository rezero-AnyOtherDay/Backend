package com.rezero.anyotherday.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 설정
 * AI 서버 호출을 위한 HTTP 클라이언트
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }

    /**
     * 요청/응답 로깅을 위한 BufferingClientHttpRequestFactory 설정
     * 타임아웃: 연결 10초, 읽기 300초(5분)
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(300000);
        return new BufferingClientHttpRequestFactory(factory);
    }

    /**
     * ObjectMapper 설정
     * AI 서버 응답에 예상치 못한 필드가 있을 경우를 대비해
     * 알 수 없는 속성을 무시하도록 설정
     * null 필드도 JSON 직렬화할 때 포함하도록 설정
     * Java 8 LocalDateTime 타입 지원
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Java 8 LocalDateTime 지원 (jackson-datatype-jsr310)
        mapper.registerModule(new JavaTimeModule());
        // AI 서버 응답에 예상하지 못한 필드가 있어도 무시
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // null 값도 허용
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        // JSON 직렬화할 때 null 필드도 포함 (프론트에서 질병과 설명을 매칭하기 위해 필수)
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS);
        return mapper;
    }

    /**
     * Spring MVC의 기본 ObjectMapper 커스터마이징
     * LocalDateTime을 ISO 문자열 형식으로 직렬화하도록 설정
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // Java 8 LocalDateTime 지원
            builder.modules(new JavaTimeModule());
            // null 필드도 포함
            builder.serializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS);
        };
    }
}
