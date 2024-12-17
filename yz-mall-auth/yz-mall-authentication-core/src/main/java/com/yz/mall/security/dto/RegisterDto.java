package com.yz.mall.security.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 注册信息
 *
 * @author yunze
 * @date 2024/12/51 12:51
 */
@Data
public class RegisterDto implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 登录账号
     */
    @Length(min = 2, max = 10)
    @NotEmpty(message = "请输入账号")
    private String username;

    /**
     * 登录密码
     */
    @Length(min = 8, max = 255)
    @NotEmpty(message = "请输入登录密码")
    private String password;

    /**
     * 手机号
     */
    @Length(min = 8, max = 11)
    @NotEmpty(message = "请输入手机号")
    private String phone;
}
