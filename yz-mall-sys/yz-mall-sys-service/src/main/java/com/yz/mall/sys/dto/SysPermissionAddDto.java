package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-权限数据表(SysPermission)表新增数据模型类
 *
 * @author yunze
 * @since 2024-11-17 20:08:25
 */
@Data
public class SysPermissionAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 权限编码
     */
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 所属角色Id
     */
    @NotNull(message = "所属角色Id不能为空")
    private Long roleId;


}

