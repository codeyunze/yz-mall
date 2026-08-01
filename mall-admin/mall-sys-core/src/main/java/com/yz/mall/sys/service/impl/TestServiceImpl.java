package com.yz.mall.sys.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.entity.Test;
import com.yz.mall.sys.mapper.TestMapper;
import com.yz.mall.sys.service.TestService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.yz.mall.sys.SysCoreConfig.ASYNC_EXECUTOR;

/**
 * (Test)表服务实现类
 *
 * @author yunze
 * @since 2026-04-20 19:23:22
 */
@DS("#session.tenantCode")
@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {


    @Async(ASYNC_EXECUTOR)
    @Override
    public CompletableFuture<String> taskA() throws InterruptedException {
        // 随机睡眠1000~5000毫秒；类上 @DS("#session.tenantCode") 会在异步线程解析 Session
        long sleepTime = (long) (Math.random() * 4000 + 1000);
        Thread.sleep(sleepTime);
        return CompletableFuture.completedFuture("taskA: " + sleepTime);
    }
}

