package com.yz.mall.sys.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 第三方客户端接口授权(SysOpenClientAuth)表实体类
 *
 * @author yunze
 */
@Data
@TableName("sys_open_client_auth")
@EqualsAndHashCode(callSuper = true)
public class SysOpenClientAuth extends Model<SysOpenClientAuth> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 客户端标识
     */
    private String clientId;

    /**
     * 开放API权限码，如open:tw:vehicle:query
     */
    private String permissionCode;

    /**
     * 授权状态：0撤销 1有效
     */
    private Integer authStatus;

    /**
     * 授权时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime grantTime;

    /**
     * 过期时间，空长期
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime expireTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人用户ID
     */
    private Long createId;

    /**
     * 更新人用户ID
     */
    private Long updateId;

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
    @TableLogic(value = "0", delval = "current_timestamp")
    private Long invalid;

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
