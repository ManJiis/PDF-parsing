package com.example.pdfdemo.config;

import cn.hutool.core.thread.NamedThreadFactory;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用自定义的线程池执行异步任务 , 并设置定时任务的异步处理
 *
 * @author musui
 */
@Data
@EnableScheduling
@EnableAsync
@Component
@Configuration
@ConfigurationProperties("executor.pool")
public class ExecutorConfig implements SchedulingConfigurer, AsyncConfigurer {
    private final static Logger log = LoggerFactory.getLogger(ExecutorConfig.class);

    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
    private Integer keepAliveSecond;
    private String threadNamePrefix;

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

    /**
     * 并行任务使用策略：多线程处理
     *
     * @return ThreadPoolTaskScheduler 线程池
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskParallelScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("Timing-task-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        TaskScheduler taskScheduler = taskParallelScheduler();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }

    /**
     * 使用spring注解 @Async 需要建立 ThreadPoolTaskExecutor 这个是spring实现的线程池
     * org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
     *
     * @return Executor
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSecond);
        // 线程池拒绝策略
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }

    /**
     * 异步任务中异常处理
     *
     * @see org.springframework.scheduling.annotation.AsyncConfigurer#getAsyncUncaughtExceptionHandler()
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return ((throwable, method, objects) -> {
            log.error("==========================" + throwable.getMessage() + "=======================", throwable);
            log.error("exception method:{}", method.getName());
        });
    }
}
