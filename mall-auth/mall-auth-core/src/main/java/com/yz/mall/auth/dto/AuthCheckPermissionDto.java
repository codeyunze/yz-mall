package com.yz.mall.auth.dto;

import com.yz.mall.base.enums.MenuTypeEnum;
import lombok.Data;

/**
 * @author yunze
 * @since 2025/7/23 17:52
 */
@Data
public class AuthCheckPermissionDto {

    /**
     * 权限类型
     */
    private MenuTypeEnum type;

    /**
     * 权限标识
     */
    private String permission;
}
