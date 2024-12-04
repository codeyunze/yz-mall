package com.yz.mall.sys.controller;


import com.yz.mall.sys.dto.SysRoleRelationMenuAddDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuBindDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuQueryDto;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import com.yz.mall.sys.service.SysRoleRelationMenuService;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
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
     * 新增
     */
    @Deprecated
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysRoleRelationMenuAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @Deprecated
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
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
    @GetMapping("getRoleMenus/{roleId}")
    public Result<List<String>> getRoleMenus(@PathVariable Long roleId) {
        return success(this.service.getMenuIdsByRoleId(roleId));
    }

    /**
     * 给角色分配菜单
     *
     * @apiNote 为指定的角色绑定菜单
     */
    @PostMapping("bind")
    public Result<Boolean> bind(@RequestBody @Valid SysRoleRelationMenuBindDto dto) {
        return success(this.service.bind(dto));
    }
}

