package com.yz.mall.sys.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * SaaS-租户主表(saas_tenant)实体类
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_tenant")
public class SaasTenant extends Model<SaasTenant> {

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

    private String tenantCode;

    private String tenantName;

    private String contactName;

    private String contactPhone;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime expireTime;

    private Integer tenantStatus;

    private String remark;

    @TableField(exist = false)
    private String serviceCode;

    @TableField(exist = false)
    private String dbType;

    @TableField(exist = false)
    private String dbHost;

    @TableField(exist = false)
    private Integer dbPort;

    @TableField(exist = false)
    private String dbName;

    @TableField(exist = false)
    private String dbUsername;

    @TableField(exist = false)
    private String dbPasswordEnc;

    @TableField(exist = false)
    private String dbParams;

    @TableField(exist = false)
    private Integer dsStatus;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
