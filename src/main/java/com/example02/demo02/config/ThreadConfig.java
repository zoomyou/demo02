package com.example02.demo02.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 多线程（线程池）配置类
 */
@Configuration
@ComponentScan("com.example02.demo02.service")
@EnableAsync
public class ThreadConfig implements AsyncConfigurer{

    /**
     * 注入容器多线程执行对象的方法，
     * 设置核心线程数为：5000
     * 最大线程数为：10000
     * 队列容量为：20000
     * 意味着在同一时刻30000个请求来临时可以直接缓存，
     * 但是如果超过30000则会导致任务丢失无法缓存。
     * @return 多线程执行对象
     */
    @Override
    public Executor getAsyncExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5000);
        executor.setMaxPoolSize(10000);
        executor.setQueueCapacity(20000);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler(){
        return null;
    }

}
