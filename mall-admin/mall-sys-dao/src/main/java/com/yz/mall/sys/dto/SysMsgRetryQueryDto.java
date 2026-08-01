package com.yz.mall.sys.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息重试记录查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-01-20
 */
@Data
public class SysMsgRetryQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String msgId;

    /**
     * 业务数据ID
     */
    private String businessId;

    /**
     * Topic
     */
    private String topic;

    /**
     * 消息标签
     */
    private String tags;

    /**
     * 消费者组
     */
    private String consumerGroup;

    /**
     * 状态: 0重试中/1待处理/2已处理/3忽略
     */
    private Integer status;

    /**
     * 创建时间开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDateTime createTimeEnd;
}

