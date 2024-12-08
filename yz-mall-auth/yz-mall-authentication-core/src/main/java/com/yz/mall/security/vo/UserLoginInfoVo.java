package com.yz.mall.security.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录返回基础信息
 *
 * @author yunze
 * @date 2024/8/3 09:30
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class UserLoginInfoVo implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 登录用户Id
     */
    private String userId;

    /**
     * 登录用户
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 登录有效期
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime expires;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 用户角色
     */
    private List<String> roles;

    /**
     * 头像
     */
    private String avatar;
}
