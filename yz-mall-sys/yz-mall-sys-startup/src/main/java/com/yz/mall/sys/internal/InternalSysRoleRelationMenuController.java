package com.yz.mall.sys.internal;


import cn.dev33.satoken.annotation.SaIgnore;
import com.yz.mall.sys.dto.InternalRolePermissionQueryDto;
import com.yz.mall.sys.service.InternalSysRoleRelationMenuService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 内部暴露接口: 角色关联菜单信息
 *
 * @author yunze
 * @since 2024-12-09 12:25:55
 */
@RestController
@RequestMapping("internal/sys/role/menu")
public class InternalSysRoleRelationMenuController extends ApiController {

    private final InternalSysRoleRelationMenuService service;

    public InternalSysRoleRelationMenuController(InternalSysRoleRelationMenuService service) {
        this.service = service;
    }

    /**
     * 获取指定角色所拥有的按钮权限
     *
     * @param query 用户拥有的角色Id和查询的菜单权限类型
     * @return 按钮权限标识  <角色Id，List<权限标识>>
     */
    @SaIgnore
    @PostMapping("getPermissionsByRoleIds")
    public Result<Map<String, List<String>>> getPermissionsByRoleIds(@Valid @RequestBody InternalRolePermissionQueryDto query) {
        return success(service.getPermissionsByRoleIds(query));
    }

}

