package com.yz.unqid.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 序列号缓存数据持久化
 * @author yunze
 * @date 2024/7/11 12:56
 */
@Component
public class ScheduleCachePersistence {

    @Scheduled(fixedRate = 1000 * 60)
    public void scheduleCachePersistence() {

    }
}
