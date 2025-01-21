package com.yz.mall.sys.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yunze
 * @date 2025/1/20 15:03
 */
@Slf4j
@Service
public class BTaskService extends BaseService {

    @Override
    String setMessageQueue() {
        return ScmPurchaseQueueConfig.QUEUE_NAME;
    }

    @Override
    void startTask() {
        log.info("4-业务B的开始逻辑");
    }

    @Override
    void endTask(String taskId) {
        log.info("4-业务B的结束逻辑: {}", taskId);
    }
}
