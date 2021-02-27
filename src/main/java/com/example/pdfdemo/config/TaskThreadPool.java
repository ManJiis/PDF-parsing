package com.example.pdfdemo.config;

import cn.hutool.core.thread.NamedThreadFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author musui
 */
@Data
@EnableAsync
@Configuration
@ConfigurationProperties("executor.pool")
public class TaskThreadPool {

    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
    private Integer keepAliveSecond;
    private String threadNamePrefix;


    /**
     * 使用spring注解 @Async 需要建立 ThreadPoolTaskExecutor 这个是spring实现的线程池
     * org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
     *
     * @return ThreadPoolTaskExecutor
     */
    @Bean("taskThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor taskThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSecond);
        // 线程池拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }

    /**
     * java.util.concurrent.ThreadPoolExecutor;
     *
     * @return ThreadPoolExecutor
     */
    @Bean("taskThreadPoolExecutor")
    public ThreadPoolExecutor taskThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                Integer.MAX_VALUE,
                keepAliveSecond,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new NamedThreadFactory(threadNamePrefix, false),
                // 拒绝策略
                new ThreadPoolExecutor.AbortPolicy());
    }
}
