package com.yz.cases.mall.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统-数据源信息(SysDatasource)表实体类
 *
 * @author yunze
 * @since 2024-06-12 11:02:09
 */
@Data
public class SysDatasource extends Model<SysDatasource> {

    /**
     * 主键标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 创建人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createdId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updatedId;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updatedTime;

    /**
     * 数据是否有效：0数据有效
     */
    @TableLogic
    private Integer invalid;

    /**
     * 数据源访问用户名
     */
    private String username;

    /**
     * 数据源访问密码
     */
    private String password;

    /**
     * 数据源驱动类
     */
    private String driverClassName;

    /**
     * 数据源访问地址
     */
    private String url;

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

