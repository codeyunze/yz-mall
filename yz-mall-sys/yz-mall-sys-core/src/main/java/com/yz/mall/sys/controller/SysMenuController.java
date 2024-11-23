package com.yz.mall.sys.controller;


import com.yz.mall.sys.dto.SysMenuAddDto;
import com.yz.mall.sys.dto.SysMenuQueryDto;
import com.yz.mall.sys.dto.SysMenuUpdateDto;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.service.SysMenuService;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统-菜单资源表(SysMenu)表控制层
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
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysMenuAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysMenuUpdateDto dto) {
        boolean updated = this.service.update(dto);
        return updated ? success(true) : error(CodeEnum.BUSINESS_ERROR);
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
    public Result<List<SysMenu>> list(@RequestBody @Valid SysMenuQueryDto filter) {
        return success(this.service.page(filter));
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysMenu> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

