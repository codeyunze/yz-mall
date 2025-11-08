package com.yz.base;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yunze
 * @since 2025/10/8 19:52
 */
public class WaitAndNotifyDemo {

    public static void main(String[] args) {
        Object obj = new Object();

        // 公司资金是否到账
        AtomicBoolean flag = new AtomicBoolean(false);

        new Thread(() -> {
            synchronized (obj) {
                System.out.println("线程1 准备领取工资");
                while (!flag.get()) {
                    // 只有当flag为true时（资金到账），线程1才继续往下执行，否则循环等待
                    System.out.println("线程1 等待公司资金中");
                    try {
                        obj.wait();
                        System.out.println("线程1 公司资金到账");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("线程1 工资领取成功");
            }
        }).start();

        new Thread(() -> {
            synchronized (obj) {
                System.out.println("线程2 准备领取工资");
                while (!flag.get()) {
                    // 只有当flag为true时（资金到账），线程2才继续往下执行，否则循环等待
                    System.out.println("线程2 等待公司资金中");
                    try {
                        obj.wait();
                        System.out.println("线程2 公司资金到账");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("线程2 工资领取成功");
            }
        }).start();

        new Thread(() -> {
            synchronized (obj) {
                int day = 0;

                // 只有1号公司的资金才到账
                while (day != 1) {
                    System.out.println("线程3 公司正在准备资金 " + day + "号");
                    // 生成一个1~9的随机整数
                    day = new Random().nextInt(9) + 1;
                }

                flag.set(true);
                obj.notifyAll();
                System.out.println("线程3 公司资金到账");
            }
        }).start();
    }
}
