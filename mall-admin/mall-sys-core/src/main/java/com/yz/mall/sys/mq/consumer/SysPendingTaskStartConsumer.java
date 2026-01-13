package com.yz.mall.sys.mq.consumer;

import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.sys.AbstractSysPendingTasksQueueConfig;
import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import com.yz.mall.sys.service.ExtendSysMsgRetryService;
import com.yz.mall.sys.service.SysPendingTasksService;
import com.yz.mall.rocketmq.utils.MsgConsumerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
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
public class SysPendingTaskStartConsumer implements RocketMQListener<MessageExt> {

    private final SysPendingTasksService sysPendingTasksService;
    private final ExtendSysMsgRetryService extendSysMsgRetryService;

    public SysPendingTaskStartConsumer(SysPendingTasksService sysPendingTasksService,
                                       ExtendSysMsgRetryService extendSysMsgRetryService) {
        this.sysPendingTasksService = sysPendingTasksService;
        this.extendSysMsgRetryService = extendSysMsgRetryService;
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        MsgConsumerHelper.consumeMessage(
                messageExt,
                "consumer-mall-sys",
                extendSysMsgRetryService,
                ExtendSysPendingTasksAddDto.class,
                dto -> {
                    Long id = sysPendingTasksService.save(dto);
                    if (id == null) {
                        throw new BusinessException("待办任务创建失败");
                    }
                    log.info("消费【待办任务开始】消息成功，taskId: {}", id);
                    return dto.getBusinessId();
                }
        );
    }
}
