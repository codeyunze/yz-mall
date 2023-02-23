package com.yz.openinterface.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : yunze
 * @date : 2023/2/23 23:43
 */
@Configuration
@EnableAsync
@Data
public class ExecutorConfig {

    // 核心线程数
    // @Value("${async.executor.thread.core_pool_size}")
    private int corePoolSize = 10;

    // 最大线程数
    // @Value("${async.executor.thread.max_pool_size}")
    private int maxPoolSize = 20;

    // 队列大小
    // @Value("${async.executor.thread.queue_capacity}")
    private int queueCapacity = 999;

    // 线程名称前缀
    // @Value("${async.executor.thread.name.prefix}")
    private String namePrefix = "async-service-";

    @Bean
    public ThreadPoolTaskExecutor asyncServiceExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setKeepAliveSeconds(3);
        threadPoolTaskExecutor.setThreadNamePrefix(namePrefix);

        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        // 加载
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
