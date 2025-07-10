package com.yz.mall.sys.vo;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@EqualsAndHashCode(callSuper = true)
public class SysUserVo extends Model<SysUserVo> {

    /**
     * 主键标识
     */
    private String id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 昵称
     */
    private String username;

    /**
     * 状态1-启用,0-停用 {@link com.yz.mall.sys.enums.EnableEnum}
     */
    private Integer status;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别 {@link com.yz.mall.sys.enums.SexEnum}
     */
    private Integer sex;
}

