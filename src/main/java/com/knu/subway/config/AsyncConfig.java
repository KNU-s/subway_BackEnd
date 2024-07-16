package com.knu.subway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);  // 코어 풀 크기 설정
        executor.setMaxPoolSize(10);  // 최대 풀 크기 설정
        executor.setQueueCapacity(100);  // 큐 용량 설정
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}

