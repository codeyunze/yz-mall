package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 应用配置(SysApplication)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-08-11 20:10:15
 */
@Data
public class SysApplicationQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    private String id;

    /**
     * 创建人
     */
    private String createdId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 应用id
     */
    private String clientId;

    /**
     * 应用密钥
     */
    private String clientSecret;

    /**
     * 应用名称
     */
    private String clientName;

    /**
     * 备注说明信息
     */
    private String remark;


}

