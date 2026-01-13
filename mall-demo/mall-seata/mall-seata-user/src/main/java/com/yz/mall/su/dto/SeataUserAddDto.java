package com.yz.mall.su.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


/**
 * 分布式事务-用户表(SeataUser)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-18 00:00:00
 */
@Data
public class SeataUserAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 用户 Id
     */
    @NotNull(message = "用户 Id 不能为空")
    private Long userId;

    /**
     * 用户余额，单位：分
     */
    @NotNull(message = "用户余额(单位：分)不能为空")
    private Long balanceCents;
}