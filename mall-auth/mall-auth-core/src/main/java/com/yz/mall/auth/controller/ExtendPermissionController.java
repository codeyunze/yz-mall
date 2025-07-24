package com.yz.mall.auth.controller;

import com.yz.mall.auth.dto.AuthRolePermissionQueryDto;
import com.yz.mall.auth.service.ExtendPermissionService;
import com.yz.mall.base.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author yunze
 * @since 2025/7/24 11:55
 */
@RestController
@RequestMapping("/extend/permission")
public class ExtendPermissionController {

    private final ExtendPermissionService extendPermissionService;

    public ExtendPermissionController(ExtendPermissionService extendPermissionService) {
        this.extendPermissionService = extendPermissionService;
    }

    /**
     * 获取指定角色，指定菜单类型所拥有的权限
     *
     * @param query 用户拥有的角色Id和查询的菜单权限类型
     * @return 权限标识 <角色Id，List<权限标识>>
     */
    @PostMapping("getPermissionsByRoleIds")
    public Result<Map<String, List<String>>> getPermissionsByRoleIds(@Valid AuthRolePermissionQueryDto query) {
        return Result.success(extendPermissionService.getPermissionsByRoleIds(query));
    }
}
