package com.yz.base.threadPool;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * @author yunze
 * @since 2025/10/10 16:33
 */
public class ThreadPoolDemo2 {
    public static void main(String[] args) {
        // 拒绝策略
        RejectedExecutionHandler policy = new ThreadPoolExecutor.AbortPolicy();
        // 等待队列
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(25);
        // 核心线程数
        int corePoolSize = 3;
        // 最大线程数
        int maximumPoolSize = 5;
        // 线程存活时间
        long keepAliveTime = 10;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // executor.setQueueCapacity();

        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                queue,
                policy
        );

        for (int i = 0; i < 30; i++) {
            // executorService.execute(new PayTask());
            executorService.execute(() -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("线程池执行任务：" + Thread.currentThread().getName() + " 时间: " + LocalDateTime.now());
            });
        }
        System.out.println("核心线程数：" + executorService.getCorePoolSize()
                + " " +"最大线程数：" + executorService.getMaximumPoolSize()
                +" " + "活跃线程数：" + executorService.getActiveCount());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        executorService.setMaximumPoolSize(10);
        executorService.setCorePoolSize(8);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("核心线程数：" + executorService.getCorePoolSize()
                + " " +"最大线程数：" + executorService.getMaximumPoolSize()
                +" " + "活跃线程数：" + executorService.getActiveCount());
    }
}
