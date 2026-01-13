package com.yz.mall.sys.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 测试任务
 * @author yunze
 * @since 2025/11/16 17:57
 */
@Slf4j
@Component
public class TestJob {

    @XxlJob(value = "printJobHandler", init = "init", destroy = "destroy")
    public void printJobHandler() throws Exception {
        String param = XxlJobHelper.getJobParam();
        XxlJobHelper.log("XXL-JOB, Hello World!!!");
        XxlJobHelper.log("参数:" + param);
        for (int i = 0; i < 5; i++) {
            XxlJobHelper.log("beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }
        // default success
    }

    public void init() {
        log.info("printJobHandler 定时任务 init 初始化");
    }

    public void destroy() {
        log.info("printJobHandler 执行任务线程销毁");
    }
}
