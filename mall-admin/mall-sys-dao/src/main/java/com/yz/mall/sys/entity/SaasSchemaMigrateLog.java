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
 * 租户 schema 增量迁移执行日志(saas_schema_migrate_log)实体类
 *
 * @author yunze
 * @since 2026-08-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_schema_migrate_log")
public class SaasSchemaMigrateLog extends Model<SaasSchemaMigrateLog> {

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

    private String scriptName;

    private String scriptChecksum;

    /**
     * 执行状态：1成功 2失败 3已回滚
     */
    private Integer execStatus;

    private String errorMsg;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime execTime;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime rollbackTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
