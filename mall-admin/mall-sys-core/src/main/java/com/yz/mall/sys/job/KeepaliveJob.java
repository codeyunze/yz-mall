package com.yz.mall.sys.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import com.yz.mall.sys.service.SysAreaService;
import org.springframework.stereotype.Component;

/**
 * 数据库连接保活任务
 *
 * @author yunze
 * @date 2025/11/30 星期日 21:57
 */
@Component
public class KeepaliveJob {

    private final SysAreaService areaService;

    public KeepaliveJob(SysAreaService areaService) {
        this.areaService = areaService;
    }

    @XxlJob(value = "keepaliveJobHandler")
    public void keepaliveJobHandler() throws Exception {
        areaService.keepalive();
    }

}
