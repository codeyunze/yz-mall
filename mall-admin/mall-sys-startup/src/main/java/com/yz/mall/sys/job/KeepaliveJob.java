package com.yz.mall.sys.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import com.yz.mall.sys.service.SysAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 数据库连接保活任务
 *
 * @author yunze
 * @date 2025/11/30 星期日 21:57
 */
@Slf4j
@Component
public class KeepaliveJob {

    private final SysAreaService areaService;

    public KeepaliveJob(SysAreaService areaService) {
        this.areaService = areaService;
    }

    @XxlJob(value = "keepaliveJobHandler", init = "init", destroy = "destroy")
    public void keepaliveJobHandler() throws Exception {
        log.info("keepaliveJobHandler 执行任务");
        areaService.keepalive();
    }

    public void init() {
        log.info("keepaliveJobHandler 定时任务 init 初始化");
    }

    public void destroy() {
        log.info("keepaliveJobHandler 执行任务线程销毁");
    }
}
