package com.yz.base.threadPool;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;

import java.util.concurrent.*;

/**
 * @author yunze
 * @since 2025/10/10 16:33
 */
public class ThreadPoolDemo3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 拒绝策略
        RejectedExecutionHandler policy = new ThreadPoolExecutor.CallerRunsPolicy();
        // 等待队列
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(5);
        // 核心线程数
        int corePoolSize = 3;
        // 最大线程数
        int maximumPoolSize = 5;
        // 线程存活时间
        long keepAliveTime = 10;

        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                queue,
                policy
        );

        PayTask payTask = new PayTask();
        for (int i = 0; i < 9; i++) {
            // executorService.execute(new PayTask());
            Future<?> future = executorService.submit(payTask);
        }

        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                System.out.println("线程池执行任务：" + Thread.currentThread().getName());
                return "执行成功";
            }
        };
        Future future = executorService.submit(callable);
        System.out.println(future.get());


        SM2 sm2 = SmUtil.sm2();

    }
}
