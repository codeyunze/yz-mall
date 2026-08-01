package com.yz.mall.sys.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SaaS-租户初始化任务表(saas_tenant_init_task)实体类
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_tenant_init_task")
public class SaasTenantInitTask extends Model<SaasTenantInitTask> {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "current_timestamp")
    private Long invalid;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    private String tenantCode;

    private String serviceCode;

    private String taskNo;

    private Integer taskType;

    private Integer taskStatus;

    private String stepCode;

    private String errorMsg;

    private Integer retryCount;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime finishedTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
