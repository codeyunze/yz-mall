package com.yz.mall.sys.entity;

import java.time.LocalDateTime;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 系统-组织表(SysOrg)表实体类
 *
 * @author yunze
 * @since 2024-11-17 20:19:07
 */
@Data
public class SysOrg extends Model<SysOrg> {

    /**
     * 主键标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

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
     * 数据是否有效：0数据有效
     */
    @TableLogic
    private Integer invalid;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织所属用户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 上级组织
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    /**
     * 状态1-启用,0-停用 {@link com.yz.mall.sys.enums.EnableEnum}
     */
    private Integer status;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 组织路径
     */
    private String orgPathId;

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

