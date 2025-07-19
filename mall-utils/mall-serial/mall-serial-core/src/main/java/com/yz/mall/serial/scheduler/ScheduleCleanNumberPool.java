package com.yz.mall.serial.scheduler;

import com.yz.mall.serial.SerialHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 清理号池
 *
 * @author yunze
 * @date 2024/7/17 23:52
 */
@Component
public class ScheduleCleanNumberPool {

    private static final Logger log = LoggerFactory.getLogger(ScheduleCleanNumberPool.class);

    /**
     * 每天0点清理一次流水号号池
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleCleanNumberPool() {
        log.info("清理号池过期流水号");
        SerialHolder.cleanExpiredNumber();
    }
}
