package com.yz.mall.auth.feign;

import com.yz.mall.auth.dto.AuthRolePermissionQueryDto;
import com.yz.mall.base.Result;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

/**
 * @author yunze
 * @since 2025/7/24 14:08
 */
@FeignClient(name = "mall-sys", contextId = "extendPermission", path = "extend/permission")
public interface ExtendPermissionFeign {

    /**
     * 获取指定角色，指定菜单类型所拥有的权限
     *
     * @param query 用户拥有的角色Id和查询的菜单权限类型
     * @return 权限标识 <角色Id，List<权限标识>>
     */
    @PostMapping("getPermissionsByRoleIds")
    Result<Map<String, List<String>>> getPermissionsByRoleIds(@Valid AuthRolePermissionQueryDto query);
}
