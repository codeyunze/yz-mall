package com.yz.mall.rocketmq.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import com.yz.mall.sys.dto.MsgRetryRecordDto;
import com.yz.mall.sys.enums.MsgRetryStatusEnum;
import com.yz.mall.sys.service.ExtendSysMsgRetryService;
import com.yz.mall.sys.utils.ExceptionRetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * 消息消费辅助工具类
 * 封装消息消费的通用逻辑：幂等性检查、异常处理、失败记录
 *
 * @author yunze
 * @since 2025-01-20
 */
@Slf4j
public class MsgConsumerHelper {

    /**
     * 执行消息消费，自动处理幂等性检查和异常记录
     *
     * @param messageExt               消息对象
     * @param consumerGroup            消费者组
     * @param extendSysMsgRetryService 消息重试服务
     * @param dtoClass                 消息体类型
     * @param businessHandler          业务处理函数，接收解析后的DTO，返回业务ID（用于失败记录）
     * @param <T>                      消息体类型
     * @throws BusinessException 业务异常
     */
    public static <T> void consumeMessage(MessageExt messageExt,
                                          String consumerGroup,
                                          ExtendSysMsgRetryService extendSysMsgRetryService,
                                          Class<T> dtoClass,
                                          Function<T, String> businessHandler) {
        String msgId = messageExt.getMsgId();
        String topic = messageExt.getTopic();
        String tags = messageExt.getTags();
        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);

        try {
            // 幂等性检查：如果消息已处理、待处理（重试次数用完）或已忽略，直接返回确认消息
            // 这样可以让消息从RocketMQ队列中移除，避免堆积
            Integer status = extendSysMsgRetryService.getStatusByMsgId(msgId);
            if (status != null) {
                if (MsgRetryStatusEnum.PROCESSED.get().equals(status)) {
                    log.info("消息已处理，确认消息并从队列移除，msgId: {}", msgId);
                    return;
                }
                if (MsgRetryStatusEnum.PENDING.get().equals(status)) {
                    log.warn("消息重试次数已用完，状态为待处理，确认消息并从队列移除，msgId: {}", msgId);
                    return;
                }
                if (MsgRetryStatusEnum.IGNORED.get().equals(status)) {
                    log.info("消息已忽略，确认消息并从队列移除，msgId: {}", msgId);
                    return;
                }
            }

            // 解析消息体
            T dto = JacksonUtil.getObjectMapper().readValue(body, dtoClass);

            // 执行业务逻辑
            String businessId = businessHandler.apply(dto);

            // 如果之前有失败记录，标记为已处理
            if (status != null && !MsgRetryStatusEnum.PROCESSED.get().equals(status)) {
                extendSysMsgRetryService.markAsProcessedByMsgId(msgId);
            }

            log.info("消费消息成功，msgId: {}, businessId: {}", msgId, businessId);
        } catch (JsonProcessingException e) {
            recordAndThrow(msgId, topic, tags, body, consumerGroup, extendSysMsgRetryService,
                    null, e, false, "解析消息失败");
        } catch (BusinessException e) {
            handleBusinessException(msgId, topic, tags, body, consumerGroup, extendSysMsgRetryService, e, dtoClass);
        } catch (Exception e) {
            recordAndThrow(msgId, topic, tags, body, consumerGroup, extendSysMsgRetryService,
                    null, e, ExceptionRetryUtil.isRetryable(e), "消费消息异常");
        }
    }

    /**
     * 处理业务异常
     */
    private static <T> void handleBusinessException(String msgId, String topic, String tags, String body,
                                                    String consumerGroup,
                                                    ExtendSysMsgRetryService extendSysMsgRetryService,
                                                    BusinessException e, Class<T> dtoClass) {
        boolean retryable = ExceptionRetryUtil.isRetryable(e);
        log.error("消费消息业务异常，msgId: {}, retryable: {}", msgId, retryable, e);
        String businessId = extractBusinessId(body, dtoClass);
        recordFailure(msgId, topic, tags, body, consumerGroup, businessId, e, retryable, extendSysMsgRetryService);
        throw e;
    }

    /**
     * 记录失败并抛出异常
     */
    private static void recordAndThrow(String msgId, String topic, String tags, String body,
                                      String consumerGroup,
                                      ExtendSysMsgRetryService extendSysMsgRetryService,
                                      String businessId, Throwable e, boolean retryable, String errorPrefix) {
        log.error("{}，msgId: {}, retryable: {}", errorPrefix, msgId, retryable, e);
        recordFailure(msgId, topic, tags, body, consumerGroup, businessId, e, retryable, extendSysMsgRetryService);
        throw new BusinessException(errorPrefix + "：" + e.getMessage());
    }

    /**
     * 记录失败
     */
    private static void recordFailure(String msgId, String topic, String tags, String body,
                                     String consumerGroup, String businessId, Throwable exception,
                                     boolean retryable, ExtendSysMsgRetryService extendSysMsgRetryService) {
        MsgRetryRecordDto recordDto = new MsgRetryRecordDto(msgId, topic, tags, body, consumerGroup,
                businessId, exception, retryable);
        extendSysMsgRetryService.recordFailure(recordDto);
    }

    /**
     * 从消息体中提取业务ID
     */
    private static <T> String extractBusinessId(String body, Class<T> dtoClass) {
        try {
            T dto = JacksonUtil.getObjectMapper().readValue(body, dtoClass);
            if (dto instanceof ExtendSysPendingTasksAddDto) {
                return ((ExtendSysPendingTasksAddDto) dto).getBusinessId();
            }
        } catch (JsonProcessingException ex) {
            // 忽略解析异常
        }
        return null;
    }
}

