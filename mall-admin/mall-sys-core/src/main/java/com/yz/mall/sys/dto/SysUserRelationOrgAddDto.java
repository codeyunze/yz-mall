package com.yz.mall.sys.dto;

import com.yz.mall.sys.entity.SysOrg;
import com.yz.mall.sys.entity.SysUser;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-用户关联组织数据表(SysUserRelationOrg)表新增数据模型类
 *
 * @author yunze
 * @since 2024-11-17 20:26:16
 */
@Data
public class SysUserRelationOrgAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id {@link SysUser#getId()}
     */
    @NotNull(message = "用户Id不能为空")
    private Long userId;

    /**
     * 组织Id {@link SysOrg#getId()}
     */
    @NotNull(message = "组织Id不能为空")
    private Long orgId;


}

