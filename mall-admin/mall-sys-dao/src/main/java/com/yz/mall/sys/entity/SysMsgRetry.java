package com.yz.mall.sys.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息重试记录表(SysMsgRetry)表实体类
 *
 * @author yunze
 * @since 2025-01-20
 */
@Data
public class SysMsgRetry extends Model<SysMsgRetry> {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 业务数据ID，业务里唯一
     */
    private String businessId;

    /**
     * 消息ID，MQ消息里唯一
     */
    private String msgId;

    /**
     * 原始Topic
     */
    private String topic;

    /**
     * 消息标签
     */
    private String tags;

    /**
     * 消息内容
     */
    private String body;

    /**
     * 异常信息
     */
    private String exception;

    /**
     * 剩余重试次数，默认5
     */
    private Integer retryCount;

    /**
     * 重试历史，JSON格式
     */
    private String retryHistory;

    /**
     * 状态: 0重试中/1待处理/2已处理/3忽略
     */
    private Integer status;

    /**
     * 消费者组
     */
    private String consumerGroup;

    /**
     * 下次重试时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime nextRetryTime;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}

