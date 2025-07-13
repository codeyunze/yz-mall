package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 系统-用户与组织关联角色表(SysUserRelationRole)表新增数据模型类
 *
 * @author yunze
 * @since 2024-11-26 11:46:13
 */
@Data
public class SysUserRelationRoleBindDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联用户Id/组织Id
     */
    @NotNull(message = "关联用户Id/组织Id不能为空")
    private Long relationId;

    /**
     * 关联角色Id
     */
    @NotNull(message = "关联角色Id不能为空")
    private List<Long> roleIds;

    /**
     * 用户还是组织（默认为用户）
     * 0: 用户；
     * 1: 组织；
     */
    private Integer type = 0;

}

