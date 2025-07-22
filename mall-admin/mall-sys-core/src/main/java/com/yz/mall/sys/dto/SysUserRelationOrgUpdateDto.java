package com.yz.mall.sys.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-用户关联组织数据表(SysUserRelationOrg)表更新数据模型类
 *
 * @author yunze
 * @since 2024-11-17 20:26:16
 */
@Data
public class SysUserRelationOrgUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 组织Id
     */
    private Long orgId;


}

