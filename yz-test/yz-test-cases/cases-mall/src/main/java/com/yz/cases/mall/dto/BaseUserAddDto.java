package com.yz.cases.mall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 基础-用户(BaseUser)表实体类
 *
 * @author yunze
 * @since 2024-06-11 23:16:13
 */
@Data
public class BaseUserAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

}

