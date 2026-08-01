package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.sys.dto.SysMenuAddDto;
import com.yz.mall.sys.dto.SysMenuQueryDto;
import com.yz.mall.sys.dto.SysMenuUpdateDto;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.service.SysMenuService;
import com.yz.mall.sys.vo.SysMenuSlimVo;
import com.yz.mall.web.annotation.RepeatSubmit;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 系统-菜单资源
 *
 * @author yunze
 * @since 2024-11-21 23:29:01
 */
@RestController
@RequestMapping("sys/menu")
public class SysMenuController extends ApiController {

    /**
     * 服务对象
     */
    private final SysMenuService service;

    public SysMenuController(SysMenuService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:menu:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysMenuAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:menu:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysMenuUpdateDto dto) {
        boolean updated = this.service.update(dto);
        return updated ? success(true) : error(CodeEnum.BUSINESS_ERROR);
    }

    /**
     * 查询所有菜单的简略信息
     */
    @SaCheckPermission("api:system:menu:list")
    @PostMapping("listSlim")
    public Result<List<SysMenuSlimVo>> listSlim() {
        return success(this.service.listSlim());
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:menu:edit")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.recursionRemoveById(id));
    }

    /**
     * 菜单列表信息查询
     */
    @SaCheckPermission("api:system:menu:list")
    @PostMapping("list")
    public Result<List<SysMenu>> list(@RequestBody @Valid SysMenuQueryDto filter) {
        return success(this.service.list(filter));
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("api:system:menu:list")
    @GetMapping("get/{id}")
    public Result<SysMenu> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

