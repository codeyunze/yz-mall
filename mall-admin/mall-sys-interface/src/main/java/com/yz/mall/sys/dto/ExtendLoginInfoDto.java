package com.yz.mall.sys.dto;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户登录返回信息
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@Data
public class ExtendLoginInfoDto implements Serializable {

    private final long serialVersionUID = 1L;

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
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别 {@link com.yz.mall.sys.enums.SexEnum}
     */
    private Integer sex;
}

