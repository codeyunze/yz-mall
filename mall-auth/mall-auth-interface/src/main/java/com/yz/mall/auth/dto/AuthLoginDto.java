package com.yz.mall.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求基础信息
 *
 * @author yunze
 * @date 2024/7/30 23:18
 */
@Data
public class AuthLoginDto implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 登录账号
     */
    @NotBlank(message = "请输入登录账号")
    private String account;

    /**
     * 登录密码
     */
    @NotBlank(message = "请输入登录密码")
    private String password;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 登录类型
     */
    private String type;
}
