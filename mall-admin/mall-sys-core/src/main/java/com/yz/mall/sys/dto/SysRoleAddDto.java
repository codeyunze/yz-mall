package com.yz.mall.sys.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统-角色数据表(SysRole)表新增数据模型类
 *
 * @author yunze
 * @since 2024-11-17 18:15:15
 */
@Data
public class SysRoleAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色编码
     */
    @Length(max = 36, message = "角色编码不能超过36个字符")
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /**
     * 角色名称
     */
    @Length(max = 36, message = "角色名称不能超过36个字符")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 备注说明
     */
    @Length(max = 100, message = "备注说明不能超过100个字符")
    private String remark;

    /**
     * 所属组织
     */
    private Long orgId;


}

