package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统-角色数据表(SysRole)表新增数据模型类
 *
 * @author yunze
 * @since 2024-11-17 18:15:15
 */
@Data
public class SysRoleAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 所属组织
     */
    private Long orgId;


}

