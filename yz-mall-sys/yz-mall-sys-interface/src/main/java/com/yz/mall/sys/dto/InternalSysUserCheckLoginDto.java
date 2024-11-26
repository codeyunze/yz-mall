package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 基础-用户(SysUser)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Data
public class InternalSysUserCheckLoginDto implements Serializable {

    private static final long serialVesionUID = 1L;

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

    public InternalSysUserCheckLoginDto() {
    }

    public InternalSysUserCheckLoginDto(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}

