package com.yz.mall.pms.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.pms.service.PmsProductService;
import com.yz.mall.sys.AbstractSysPendingTasksQueueConfig;
import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 系统管理-待办任务结束-消息消费者
 *
 * @author yunze
 * @since 2025/9/9 22:49
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = AbstractSysPendingTasksQueueConfig.TOPIC_NAME
        , consumerGroup = "consumer-mall-pms"
        , selectorType = SelectorType.TAG
        , selectorExpression = "pms_product_publish_end_key"
        , messageModel = MessageModel.CLUSTERING)
public class PmsProductPublishTaskEndConsumer implements RocketMQListener<String> {

    private final PmsProductService pmsProductService;

    public PmsProductPublishTaskEndConsumer(PmsProductService pmsProductService) {
        this.pmsProductService = pmsProductService;
    }

    @Override
    public void onMessage(String s) {
        try {
            // 使用 readValue 方法将 JSON 字符串解析为对象
            ExtendSysPendingTasksAddDto dto = JacksonUtil.getObjectMapper().readValue(s, ExtendSysPendingTasksAddDto.class);
            pmsProductService.approvedReview(Long.valueOf(dto.getBusinessId()));
            log.info("消费【待办任务结束】消息：{}", s);
        } catch (JsonProcessingException e) {
            log.error("解析待办任务结束消息失败：{}", s, e);
            throw new BusinessException("解析待办任务消息结束：" + e.getMessage());
        }
    }
}
