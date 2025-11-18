package com.yz.base.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yunze
 * @since 2025/10/9 23:31
 */
public class ThreadPoolDemo {
    public static void main(String[] args) {
        PayTask task1 = new PayTask();
        PayTask task2 = new PayTask();
        PayTask task3 = new PayTask();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(task1);
        executorService.submit(task2);
        executorService.submit(task3);
    }

}

