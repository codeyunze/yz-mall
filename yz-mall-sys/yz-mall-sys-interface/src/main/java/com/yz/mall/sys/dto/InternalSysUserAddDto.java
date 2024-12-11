package com.yz.mall.sys.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 基础-用户(SysUser)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Data
public class InternalSysUserAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 手机号
     */
    @Length(max = 11, message = "手机号不能超过11个字符")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 邮箱
     */
    @Length(max = 50, message = "邮箱不能超过50个字符")
    private String email;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 昵称
     */
    @Length(max = 10, message = "昵称不能超过10个字符")
    @NotBlank(message = "昵称不能为空")
    private String username;
}

