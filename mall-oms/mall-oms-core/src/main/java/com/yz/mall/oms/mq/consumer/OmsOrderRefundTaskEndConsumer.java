package com.yz.mall.oms.mq.consumer;

import com.yz.mall.sys.AbstractSysPendingTasksQueueConfig;
import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import com.yz.mall.sys.service.ExtendSysMsgRetryService;
import com.yz.mall.rocketmq.utils.MsgConsumerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 退款审核待办结束消息消费者。
 * <p>
 * 退款业务已在审核接口完成，此处仅消费结束消息，避免 Topic 堆积。
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = AbstractSysPendingTasksQueueConfig.TOPIC_NAME
        , consumerGroup = "consumer-mall-oms-refund-end"
        , selectorType = SelectorType.TAG
        , selectorExpression = "oms_order_refund_end_key"
        , messageModel = MessageModel.CLUSTERING)
public class OmsOrderRefundTaskEndConsumer implements RocketMQListener<MessageExt> {

    private final ExtendSysMsgRetryService extendSysMsgRetryService;

    public OmsOrderRefundTaskEndConsumer(ExtendSysMsgRetryService extendSysMsgRetryService) {
        this.extendSysMsgRetryService = extendSysMsgRetryService;
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        MsgConsumerHelper.consumeMessage(
                messageExt,
                "consumer-mall-oms-refund-end",
                extendSysMsgRetryService,
                ExtendSysPendingTasksAddDto.class,
                dto -> {
                    log.info("消费【退款待办结束】消息成功，businessId: {}, taskId: {}", dto.getBusinessId(), dto.getTaskId());
                    return dto.getBusinessId();
                }
        );
    }
}
