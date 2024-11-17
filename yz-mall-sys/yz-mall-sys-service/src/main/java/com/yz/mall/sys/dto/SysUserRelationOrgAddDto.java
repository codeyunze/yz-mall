package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-用户关联组织数据表(SysUserRelationOrg)表新增数据模型类
 *
 * @author yunze
 * @since 2024-11-17 20:26:16
 */
@Data
public class SysUserRelationOrgAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 用户Id
     */
    @NotNull(message = "用户Id不能为空")
    private Long userId;

    /**
     * 组织Id
     */
    @NotNull(message = "组织Id不能为空")
    private Long orgId;


}

