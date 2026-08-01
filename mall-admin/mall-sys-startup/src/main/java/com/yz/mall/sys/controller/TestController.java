package com.yz.mall.sys.controller;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.sys.entity.Test;
import com.yz.mall.sys.service.SaasTenantService;
import com.yz.mall.sys.service.TestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * (Test)表控制层
 *
 * @author yunze
 * @since 2026-04-20 19:23:22
 */
@RestController
@RequestMapping("sys/test")
public class TestController extends ApiController {

    /**
     * 服务对象
     */
    private final TestService service;
    private final SaasTenantService tenantService;

    public TestController(TestService service, SaasTenantService tenantService) {
        this.service = service;
        this.tenantService = tenantService;
    }

    /**
     * 新增
     */
    @RequestMapping("add")
    public Result<Boolean> insert(@RequestParam("id") Integer id
            , @RequestParam("tenantCode") String tenantCode
            , HttpServletRequest request) {
        request.getSession().setAttribute("tenantCode", tenantCode);

        Test test = new Test();
        test.setId(id);
        return success(this.service.save(test));
    }


    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<Test> page(@PathVariable Integer id) {
        return success(this.service.getById(id));
    }


    /**
     * 异步演示。{@code TestServiceImpl} 使用 {@code @DS("#session.tenantCode")}，
     * 调用前必须把租户编码写入 Session，异步线程才能解析数据源。
     *
     * @param tenantCode 动态数据源名称（与 Session 属性 tenantCode 对应）
     */
    @RequestMapping("async")
    public Result<Map<String, String>> async(@RequestParam("tenantCode") String tenantCode, HttpServletRequest request)
            throws InterruptedException, ExecutionException {
        request.getSession().setAttribute("tenantCode", tenantCode);
        Long start = System.currentTimeMillis();

        CompletableFuture<String> taskA = service.taskA();
        CompletableFuture<String> taskB = tenantService.taskB();

        taskA.join();
        taskB.join();

        Map<String, String> result = new HashMap<>();
        result.put("test", taskA.get());
        result.put("tenant", taskB.get());

        Long end = System.currentTimeMillis();
        result.put("time", String.valueOf(end - start) + "ms");

        return success(result);
    }
}

