package com.yz.mall.pms.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.sys.service.ExtendSysAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 定时加载地区信息任务
 *
 * @author yunze
 * @date 2025/11/30 星期日 21:57
 */
@Slf4j
@Component
public class LoadAreaJob {

    private final ExtendSysAreaService extendSysAreaService;

    public LoadAreaJob(ExtendSysAreaService extendSysAreaService) {
        this.extendSysAreaService = extendSysAreaService;
    }

    @XxlJob(value = "loadAreaJobHandler", init = "init", destroy = "destroy")
    public void keepaliveJobHandler() throws Exception {
        log.info("loadAreaJobHandler 执行任务");
        String jobParam = XxlJobHelper.getJobParam();
        Map<String, String> params = JacksonUtil.getObjectMapper().readValue(jobParam, Map.class);
        extendSysAreaService.getById(params.get("area"));
    }

    public void init() {
        log.info("loadAreaJobHandler 定时任务 init 初始化");
    }

    public void destroy() {
        log.info("loadAreaJobHandler 执行任务线程销毁");
    }
}
