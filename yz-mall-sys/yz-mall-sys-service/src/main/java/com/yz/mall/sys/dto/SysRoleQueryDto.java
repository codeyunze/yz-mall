package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 系统-角色数据表(SysRole)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-11-17 18:15:15
 */
@Data
public class SysRoleQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 所属组织
     */
    private Long orgId = -1L;


}

