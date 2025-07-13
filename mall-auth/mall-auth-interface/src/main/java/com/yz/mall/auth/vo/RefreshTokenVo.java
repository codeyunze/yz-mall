package com.yz.mall.auth.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 刷新令牌返回信息
 * @author yunze
 * @date 2024/11/19 星期二 22:54
 */
@Data
public class RefreshTokenVo implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 令牌有效期
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime expires;
}
