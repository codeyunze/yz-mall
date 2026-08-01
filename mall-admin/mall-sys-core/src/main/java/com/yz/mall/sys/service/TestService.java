package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.entity.Test;

import java.util.concurrent.CompletableFuture;


/**
 * (Test)表服务接口
 *
 * @author yunze
 * @since 2026-04-20 19:23:22
 */
public interface TestService extends IService<Test> {

    public CompletableFuture<String> taskA() throws InterruptedException;
}

