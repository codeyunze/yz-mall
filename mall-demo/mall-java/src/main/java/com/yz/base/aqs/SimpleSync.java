package com.yz.base.aqs;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;

/**
 * 自定义同步器
 *
 * @author yunze
 * @since 2025/10/9 00:34
 */
public class SimpleSync extends AbstractQueuedSynchronizer {

    // 尝试获取锁（独占模式）
    @Override
    protected boolean tryAcquire(int arg) {
        // 使用CAS尝试将state从0改为1
        if (compareAndSetState(0, 1)) {
            // 成功获取锁，记录当前线程
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }
        return false;
    }

    // 尝试释放锁（独占模式）
    @Override
    protected boolean tryRelease(int arg) {
        // 只有持有锁的线程才能释放
        if (Thread.currentThread() != getExclusiveOwnerThread()) {
            throw new IllegalMonitorStateException();
        }
        // 释放锁，重置状态
        setExclusiveOwnerThread(null);
        setState(0);
        return true;
    }

    // 判断是否持有锁
    protected boolean isLocked() {
        return getState() == 1;
    }

    // 提供创建条件对象的方法（直接使用AQS的内部实现）
    Condition newCondition() {
        return new ConditionObject();
    }
}
