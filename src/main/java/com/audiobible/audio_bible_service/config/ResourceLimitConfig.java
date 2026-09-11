package com.audiobible.audio_bible_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ResourceLimitConfig {
    @Value("${TASK_CORE_SIZE:1}")
    private int coreSize;
    @Value("${TASK_MAX_SIZE:2}")
    private int maxSize;
    @Value("${TASK_QUEUE_CAPACITY:1000}")
    private int queueCapacity;

    @Bean(name = "logTaskExecutor")
    public Executor logTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Math.max(1, coreSize));
        executor.setMaxPoolSize(Math.max(Math.max(1, coreSize), maxSize));
        executor.setQueueCapacity(Math.max(100, queueCapacity));
        executor.setThreadNamePrefix("log-task-");
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.initialize();
        return executor;
    }
}
