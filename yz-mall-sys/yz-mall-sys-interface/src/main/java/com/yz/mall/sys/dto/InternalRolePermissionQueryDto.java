package com.yz.mall.sys.dto;

import com.yz.mall.sys.enums.MenuTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 角色权限查询
 *
 * @author yunze
 * @date 2024/12/10 14:23
 */
@Data
public class InternalRolePermissionQueryDto implements Serializable {

    /**
     * 菜单类型
     */
    @NotNull(message = "菜单类型不能为空")
    private MenuTypeEnum menuType;

    /**
     * 角色Id
     */
    @NotNull(message = "角色不能为空")
    private List<Long> roleIds;
}
