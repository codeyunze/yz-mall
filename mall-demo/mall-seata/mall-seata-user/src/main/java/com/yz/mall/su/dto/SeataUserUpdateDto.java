package com.yz.mall.su.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


/**
 * 分布式事务-用户表(SeataUser)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-18 00:00:00
 */
@Data
public class SeataUserUpdateDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空")
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态:0-禁用,1-启用
     */
    private Integer status;

    /**
     * 用户余额，单位：分
     */
    private Long balance;
}