package com.yz.base;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yunze
 * @since 2025/10/9 00:18
 */
@Slf4j
public class VolatileExample1 {
    // 这里我们先不加 volatile 关键字
    private static volatile boolean flag = false;

    public static void main(String[] args) {
        // 线程 A：负责修改 flag 的值
        Thread threadA = new Thread(() -> {
            try {
                Thread.sleep(2000); // 让主线程先跑起来
            } catch (InterruptedException e) {
                log.error("线程中断异常", e);
            }
            flag = true; // 修改 flag 为 true
            System.out.println("Thread A: flag has been set to true");
        });

        // 线程 B：负责读取 flag 的值
        Thread threadB = new Thread(() -> {
            // 如果 flag 一直是 false，这个循环就会一直跑
            while (!flag) {
                // 这里是一个空循环
            }
            System.out.println("Thread B: flag is now true, exiting loop");
        });

        threadA.start();
        threadB.start();
    }
}
