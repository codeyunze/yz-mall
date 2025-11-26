package com.yz.mall.su.dto;

import java.time.LocalDateTime;

import lombok.Data;

import java.io.Serializable;


/**
 * 分布式事务-用户表(SeataUser)表查询数据模型类
 *
 * @author yunze
 * @since 2024-06-18 00:00:00
 */
@Data
public class SeataUserQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;
}