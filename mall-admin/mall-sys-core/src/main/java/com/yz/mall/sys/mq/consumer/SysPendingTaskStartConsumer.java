package com.yz.mall.sys.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.sys.AbstractSysPendingTasksQueueConfig;
import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import com.yz.mall.sys.service.SysPendingTasksService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 系统管理-待办任务开始-消息消费者
 *
 * @author yunze
 * @since 2025/9/9 22:49
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = AbstractSysPendingTasksQueueConfig.TOPIC_NAME
        , consumerGroup = "consumer-mall-sys"
        , selectorType = SelectorType.TAG
        , selectorExpression = "pms_product_publish_start_key"
        , messageModel = MessageModel.CLUSTERING)
public class SysPendingTaskStartConsumer implements RocketMQListener<String> {

    private final SysPendingTasksService sysPendingTasksService;

    public SysPendingTaskStartConsumer(SysPendingTasksService sysPendingTasksService) {
        this.sysPendingTasksService = sysPendingTasksService;
    }

    @Override
    public void onMessage(String s) {
        try {
            // 使用 readValue 方法将 JSON 字符串解析为对象
            ExtendSysPendingTasksAddDto dto = JacksonUtil.getObjectMapper().readValue(s, ExtendSysPendingTasksAddDto.class);
            Long id = sysPendingTasksService.save(dto);
            if (id == null) {
                log.error("待办任务创建失败：{}", s);
                throw new BusinessException("待办任务创建失败");
            }
            log.info("消费【待办任务开始】消息：{}", id);
        } catch (JsonProcessingException e) {
            log.error("解析待办任务消息失败：{}", s, e);
            throw new BusinessException("解析待办任务消息失败：" + e.getMessage());
        }
    }
}
