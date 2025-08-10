package com.yz.mall.sys.feign;

import com.yz.mall.base.Result;
import com.yz.mall.sys.dto.ExtendRolePermissionQueryDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 内部暴露接口: 角色关联菜单
 *
 * @author yunze
 * @date 2024/12/9 19:42
 */
@FeignClient(name = "yz-mall-sys", contextId = "extendSysRoleRelationMenu", path = "extend/sys/role/menu")
public interface ExtendSysRoleRelationMenuFeign {

    /**
     * 获取指定角色所拥有的按钮权限
     *
     * @param query 用户拥有的角色Id和查询的菜单权限类型
     * @return 按钮权限标识
     */
    @PostMapping("getPermissionsByRoleIds")
    Result<Map<String, List<String>>> getPermissionsByRoleIds(@Valid @RequestBody ExtendRolePermissionQueryDto query);

}
