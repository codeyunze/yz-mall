package com.yz.mall.sys.feign;

import com.yz.tools.IdsDto;
import com.yz.tools.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 内部暴露接口: 角色关联菜单
 *
 * @author yunze
 * @date 2024/12/9 19:42
 */
@FeignClient(name = "yz-mall-sys", contextId = "internalSysRoleRelationMenu", path = "internal/sys/role/menu")
public interface InternalSysRoleRelationMenuFeign {

    /**
     * 获取指定角色所拥有的按钮权限
     *
     * @param idsDto 用户拥有的角色Id
     * @return 按钮权限标识
     */
    @PostMapping("getPermissionsByRoleIds")
    Result<Map<String, List<String>>> getPermissionsByRoleIds(@Valid @RequestBody IdsDto idsDto);

}
