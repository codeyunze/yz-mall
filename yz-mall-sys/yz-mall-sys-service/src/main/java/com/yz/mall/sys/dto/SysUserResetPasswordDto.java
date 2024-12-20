package com.yz.mall.sys.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 重置用户密码
 *
 * @author yunze
 * @since 2024-12-17 13:01:56
 */
@Data
public class SysUserResetPasswordDto implements Serializable {

    private static final long serialVesionUID = 1L;

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

