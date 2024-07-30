package com.yz.mall.security.dto;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 登录基础信息
 * @author yunze
 * @date 2024/7/30 23:18
 */
public class AuthLoginDto implements Serializable {
    /**
     * 登录账号
     */
    @NotEmpty(message = "请输入登录账号")
    private String username;

    /**
     * 登录密码
     */
    @NotEmpty(message = "请输入登录密码")
    private String password;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 登录类型
     */
    private String type;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
