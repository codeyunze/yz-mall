package com.yz.mall.sys.dto;

import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 基础-用户(BaseUser)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Data
public class SysUserUpdateDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

}

