package com.yz.base.threadPool;

public class PayTask implements Runnable {

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " 开始执行支付任务...");
    }
}
