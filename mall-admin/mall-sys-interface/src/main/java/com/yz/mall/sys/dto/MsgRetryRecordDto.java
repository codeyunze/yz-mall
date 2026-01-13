package com.yz.mall.sys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息重试记录DTO
 *
 * @author yunze
 * @since 2025-01-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsgRetryRecordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String msgId;

    /**
     * Topic
     */
    private String topic;

    /**
     * 标签
     */
    private String tags;

    /**
     * 消息内容
     */
    private String body;

    /**
     * 消费者组
     */
    private String consumerGroup;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 异常信息
     */
    private Throwable exception;

    /**
     * 是否可重试
     */
    private Boolean retryable;
}

