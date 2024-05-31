package com.yz.cases.mall.job;

import com.yz.cases.mall.service.TProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时创建数据表
 *
 * @author yunze
 * @date 2024/5/25 11:09
 */
@Slf4j
@Component
public class AutoCreateTableJob {

    @Resource
    private TProductService productService;

    /**
     * 每天将实时商品表修改为历史商品表，并创建新实时商品表
     */
    // @Scheduled(fixedRate = 1000 * 60 * 60 * 12)
    @Scheduled(cron = "0 0 0 * * ?")
    // @Scheduled(cron = "0 25 * * * ?")
    public void autoCreateTable() {
        productService.createTable();
    }
}
