package com.yz.mall.pms.mq.consumer;

import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.pms.service.PmsProductService;
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
public class PmsProductPublishTaskEndConsumer implements RocketMQListener<MessageExt> {

    private final PmsProductService pmsProductService;
    private final ExtendSysMsgRetryService extendSysMsgRetryService;

    public PmsProductPublishTaskEndConsumer(PmsProductService pmsProductService,
                                            ExtendSysMsgRetryService extendSysMsgRetryService) {
        this.pmsProductService = pmsProductService;
        this.extendSysMsgRetryService = extendSysMsgRetryService;
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        MsgConsumerHelper.consumeMessage(
                messageExt,
                "consumer-mall-pms",
                extendSysMsgRetryService,
                ExtendSysPendingTasksAddDto.class,
                dto -> {
                    try {
                        pmsProductService.approvedReview(Long.valueOf(dto.getBusinessId()));
                        log.info("消费【待办任务结束】消息成功，businessId: {}", dto.getBusinessId());
                        return dto.getBusinessId();
                    } catch (NumberFormatException e) {
                        throw new BusinessException("业务ID格式错误：" + e.getMessage());
                    }
                }
        );
    }
}
