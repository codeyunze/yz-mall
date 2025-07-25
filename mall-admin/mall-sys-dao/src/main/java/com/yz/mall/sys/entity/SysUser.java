package com.yz.mall.sys.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统-用户(SysUser)表实体类
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@EqualsAndHashCode(callSuper = true)
public class SysUser extends Model<SysUser> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 数据是否有效：0数据有效
     */
    @TableLogic(value = "0", delval = "current_timestamp")
    private Long invalid;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 昵称
     */
    private String username;

    /**
     * 状态1-启用,0-停用 {@link com.yz.mall.base.enums.EnableEnum}
     */
    private Integer status;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别 {@link com.yz.mall.base.enums.SexEnum}
     */
    private Integer sex;

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

