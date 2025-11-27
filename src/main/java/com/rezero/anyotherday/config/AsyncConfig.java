package com.rezero.anyotherday.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정
 * AI 서버 호출을 비동기로 처리하기 위한 스레드 풀 설정
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 기본 스레드 개수
        executor.setCorePoolSize(5);

        // 최대 스레드 개수
        executor.setMaxPoolSize(10);

        // 대기 큐 크기
        executor.setQueueCapacity(100);

        // 스레드 풀 이름
        executor.setThreadNamePrefix("AI-Async-");

        // 대기 중인 작업이 있을 때 애플리케이션 종료 기다리기
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 마지막 작업이 완료될 때까지 최대 대기 시간 (초)
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}
