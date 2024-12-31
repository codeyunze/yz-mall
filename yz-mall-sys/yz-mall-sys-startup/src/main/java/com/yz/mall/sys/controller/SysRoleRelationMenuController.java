package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.yz.mall.sys.dto.SysRoleRelationMenuBindDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuQueryDto;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import com.yz.mall.sys.service.SysRoleRelationMenuService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统-角色关联菜单表(SysRoleRelationMenu)表控制层
 *
 * @author yunze
 * @since 2024-11-28 12:42:36
 */
@RestController
@RequestMapping("sys/role/menu")
public class SysRoleRelationMenuController extends ApiController {

    /**
     * 服务对象
     */
    private final SysRoleRelationMenuService service;

    public SysRoleRelationMenuController(SysRoleRelationMenuService service) {
        this.service = service;
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:system:roleRelationMenu:list")
    @PostMapping("list")
    public Result<List<SysRoleRelationMenu>> list(@RequestBody @Valid SysRoleRelationMenuQueryDto filter) {
        return success(this.service.list(filter));
    }

    /**
     * 获取指定角色所拥有的菜单Id
     *
     * @param roleId 角色Id
     * @return 角色所拥有的菜单
     */
    @SaCheckPermission("api:system:roleRelationMenu:getRoleMenus")
    @GetMapping("getRoleMenus/{roleId}")
    public Result<List<String>> getRoleMenus(@PathVariable Long roleId) {
        return success(this.service.getMenuIdsByRoleId(roleId));
    }

    /**
     * 给角色分配菜单
     *
     * @apiNote 为指定的角色绑定菜单
     */
    @SaCheckPermission("api:system:roleRelationMenu:bind")
    @PostMapping("bind")
    public Result<Boolean> bind(@RequestBody @Valid SysRoleRelationMenuBindDto dto) {
        return success(this.service.bind(dto));
    }
}

