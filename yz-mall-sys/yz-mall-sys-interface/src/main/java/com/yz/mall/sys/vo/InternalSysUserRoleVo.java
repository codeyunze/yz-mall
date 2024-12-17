package com.yz.mall.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户拥有的角色信息
 *
 * @author yunze
 * @since 2024-11-17 18:15:15
 */
@Data
public class InternalSysUserRoleVo implements Serializable {

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
    private Long orgId;
}

