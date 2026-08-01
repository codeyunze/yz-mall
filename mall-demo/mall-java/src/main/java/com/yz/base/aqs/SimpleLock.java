package com.yz.base.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author yunze
 * @since 2025/10/9 00:35
 */
@Slf4j
public class SimpleLock implements Lock {

    // 同步器实例，实际的锁逻辑由它实现
    private final SimpleSync sync = new SimpleSync();

    @Override
    public void lock() {
        // 调用AQS的acquire方法，会触发tryAcquire
        sync.acquire(1);
    }

    @Override
    public void unlock() {
        // 调用AQS的release方法，会触发tryRelease
        sync.release(1);
    }

    // 其他方法简化实现
    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    // 测试方法
    public static void main(String[] args) {
        SimpleLock lock = new SimpleLock();

        // 线程1获取锁
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("线程1获取到锁，执行中...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("线程中断异常", e);
            } finally {
                System.out.println("线程1释放锁");
                lock.unlock();
            }
        }).start();

        // 线程2尝试获取锁
        new Thread(() -> {
            System.out.println("线程2尝试获取锁...");
            lock.lock();
            try {
                System.out.println("线程2获取到锁，执行中...");
            } finally {
                System.out.println("线程2释放锁");
                lock.unlock();
            }
        }).start();
    }
}
