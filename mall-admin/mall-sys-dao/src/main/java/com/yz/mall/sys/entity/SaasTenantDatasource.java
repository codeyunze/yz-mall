package com.yz.mall.sys.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 租户-服务数据源配置表(saas_tenant_datasource)实体类
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_tenant_datasource")
public class SaasTenantDatasource extends Model<SaasTenantDatasource> {

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

    private String dbType;

    private String dbHost;

    private Integer dbPort;

    private String dbName;

    private String dbUsername;

    private String dbPasswordEnc;

    private String dbParams;

    private Integer dsStatus;

    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
