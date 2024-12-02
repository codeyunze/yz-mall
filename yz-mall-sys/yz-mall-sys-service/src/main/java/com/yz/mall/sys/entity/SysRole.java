package com.yz.mall.sys.entity;

import java.time.LocalDateTime;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 系统-角色数据表(SysRole)表实体类
 *
 * @author yunze
 * @since 2024-11-17 18:15:15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends Model<SysRole> {

    /**
     * 主键标识
     */
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
    private Long invalid;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 所属组织
     */
    private Long orgId;

    /**
     * 状态1-启用,0-停用 {@link com.yz.mall.sys.enums.EnableEnum}
     */
    private Integer status;

    /**
     * 备注说明
     */
    private String remark;

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

