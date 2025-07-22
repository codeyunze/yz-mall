package com.yz.mall.auth.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 基础-用户(SysUser)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Data
public class AuthSysUserCheckLoginDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录手机号
     */
    @NotNull(message = "手机号不能为空")
    private String phone;

    /**
     * 登录密码
     */
    @NotNull(message = "密码不能为空")
    private String password;

    public AuthSysUserCheckLoginDto() {
    }

    public AuthSysUserCheckLoginDto(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}

