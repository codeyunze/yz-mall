package com.yz.mall.sys.vo;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志信息
 *
 * @author yunze
 * @since 2025-12-11
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@EqualsAndHashCode(callSuper = true)
public class SysLoginLogVo extends Model<SysLoginLogVo> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 登录状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 登录行为：1-账号登录，2-手机号登录，3-第三方登录
     */
    private Integer loginType;

    /**
     * 登录时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime loginTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
}

