package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 系统-组织表(SysOrg)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-11-17 20:19:07
 */
@Data
public class SysOrgQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织所属用户
     */
    private Long userId;


}

