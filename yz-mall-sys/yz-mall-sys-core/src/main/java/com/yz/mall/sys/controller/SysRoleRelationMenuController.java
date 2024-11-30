package com.yz.mall.sys.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysRoleRelationMenuAddDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuQueryDto;
import com.yz.mall.sys.dto.SysRoleRelationMenuUpdateDto;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import com.yz.mall.sys.service.SysRoleRelationMenuService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.ResultTable;
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
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysRoleRelationMenuAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysRoleRelationMenuUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
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
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysRoleRelationMenu> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

