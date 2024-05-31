package com.yz.cases.mall.job;

import com.yz.cases.mall.service.TProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时生成商品数据
 *
 * @author yunze
 * @date 2024/5/25 11:09
 */
@Slf4j
@Component
public class AutoGenerateDataJob {

    @Resource
    private TProductService productService;

    /**
     * 定时生成商品数据
     */
    // @Scheduled(fixedDelay = 1000 * 3)
    public void autoGenerateData() {
        productService.generateData();
    }
}
