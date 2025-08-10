package com.yz.mall.sys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 重置用户密码
 *
 * @author yunze
 * @since 2024-12-17 13:01:56
 */
@Data
public class SysUserResetPasswordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 重置密码
     */
    @NotBlank(message = "重置后密码不能为空")
    private String password;
}

