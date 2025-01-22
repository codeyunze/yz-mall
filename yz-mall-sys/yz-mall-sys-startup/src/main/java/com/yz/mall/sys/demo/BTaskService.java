package com.yz.mall.sys.demo;

import com.yz.mall.sys.business.task.AbstractTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 供应链管理-消息消费
 * @author yunze
 * @date 2025/1/20 15:03
 */
@Slf4j
@Service
public class BTaskService extends AbstractTaskService {

    @Override
    public String setMessageQueue() {
        return ScmPurchaseQueueConfig.QUEUE_NAME;
    }

    @Override
    protected void startTask(Long taskId, String businessId) {
        log.info("4-业务B的开始逻辑");
    }

    @Override
    protected void endTask(Long taskId, String businessId) {
        log.info("4-业务B的结束逻辑: {}", taskId);
    }
}
