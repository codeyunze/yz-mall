package com.yz.mall.pms.service;

import com.yz.mall.pms.config.PmsProductPublishQueueConfig;
import com.yz.mall.sys.business.task.AbstractTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 待办处理-商品上架
 *
 * @author yunze
 * @date 2025/1/20 15:03
 */
@Slf4j
@Service
public class PmsProductPublishTaskService extends AbstractTaskService {

    private final PmsProductService productService;

    public PmsProductPublishTaskService(PmsProductService productService) {
        this.productService = productService;
    }

    @Override
    public String setMessageQueue() {
        return PmsProductPublishQueueConfig.QUEUE_NAME;
    }

    @Override
    protected void startTask(Long taskId, String businessId) {
        // 商品上架待办开始逻辑
        log.info("商品上架的待办开始逻辑");
        productService.pendingReview(Long.parseLong(businessId));
    }

    @Override
    protected void endTask(Long taskId, String businessId) {
        // 商品上架待办结束逻辑
        log.info("商品上架的待办结束逻辑: {}", taskId);
        productService.approvedReview(Long.parseLong(businessId));
    }
}
