package com.yz.mall.sys.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.core.executor.DtpExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dynamic TP 演示接口：提交任务并查看线程池实时参数，用于验证 Nacos 热更新是否生效。
 * <p>
 * 验证步骤：调用 {@code /submit} 打满任务 → 在 Nacos 修改 {@code mall-sys-dtp.yaml} 中 core/max → 再调 {@code /info} 观察参数变化。
 */
@Slf4j
@SaIgnore
@RestController
@RequestMapping("/sys/dtp/demo")
public class DtpDemoController extends ApiController {

    /**
     * 与 Nacos {@code mall-sys-dtp.yaml} 中 threadPoolName 保持一致
     */
    @Resource
    private DtpExecutor mallSysDemoExecutor;

    /**
     * 查看线程池当前运行参数与状态。
     *
     * @return 核心参数、队列、活跃线程等快照
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("threadPoolName", mallSysDemoExecutor.getThreadPoolName());
        snapshot.put("corePoolSize", mallSysDemoExecutor.getCorePoolSize());
        snapshot.put("maximumPoolSize", mallSysDemoExecutor.getMaximumPoolSize());
        snapshot.put("poolSize", mallSysDemoExecutor.getPoolSize());
        snapshot.put("activeCount", mallSysDemoExecutor.getActiveCount());
        snapshot.put("largestPoolSize", mallSysDemoExecutor.getLargestPoolSize());
        snapshot.put("queueClass", mallSysDemoExecutor.getQueue().getClass().getSimpleName());
        snapshot.put("queueSize", mallSysDemoExecutor.getQueue().size());
        snapshot.put("remainingCapacity", mallSysDemoExecutor.getQueue().remainingCapacity());
        snapshot.put("completedTaskCount", mallSysDemoExecutor.getCompletedTaskCount());
        snapshot.put("taskCount", mallSysDemoExecutor.getTaskCount());
        snapshot.put("rejectedHandler", mallSysDemoExecutor.getRejectedExecutionHandler().getClass().getSimpleName());
        // snapshot.put("threadNamePrefix", mallSysDemoExecutor.getThreadNamePrefix());
        return success(snapshot);
    }

    /**
     * 向动态线程池提交一批耗时任务，便于观察队列堆积与线程扩缩。
     *
     * @param tasks   任务数量，默认 20
     * @param sleepMs 每个任务休眠毫秒，默认 1000
     * @return 提交结果摘要
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submit(@RequestParam(defaultValue = "20") int tasks, @RequestParam(defaultValue = "1000") long sleepMs) {
        int safeTasks = Math.max(1, Math.min(tasks, 500));
        long safeSleepMs = Math.max(0, Math.min(sleepMs, 30_000));
        for (int i = 0; i < safeTasks; i++) {
            final int taskNo = i;
            mallSysDemoExecutor.execute(() -> {
                try {
                    log.info("task-{} is running on thread={}", taskNo, Thread.currentThread().getName());
                    Thread.sleep(safeSleepMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("submitted", safeTasks);
        result.put("sleepMs", safeSleepMs);
        result.put("corePoolSize", mallSysDemoExecutor.getCorePoolSize());
        result.put("maximumPoolSize", mallSysDemoExecutor.getMaximumPoolSize());
        result.put("activeCount", mallSysDemoExecutor.getActiveCount());
        result.put("queueSize", mallSysDemoExecutor.getQueue().size());
        return success(result);
    }
}
