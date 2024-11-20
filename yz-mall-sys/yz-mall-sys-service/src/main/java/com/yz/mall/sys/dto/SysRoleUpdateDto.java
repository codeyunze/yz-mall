package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-角色数据表(SysRole)表更新数据模型类
 *
 * @author yunze
 * @since 2024-11-17 18:15:16
 */
@Data
public class SysRoleUpdateDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

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

    /**
     * 备注说明
     */
    private String remark;


}

