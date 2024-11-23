package com.yz.mall.sys.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysRoleAddDto;
import com.yz.mall.sys.dto.SysRoleQueryDto;
import com.yz.mall.sys.dto.SysRoleUpdateDto;
import com.yz.mall.sys.entity.SysRole;
import com.yz.mall.sys.service.SysRoleService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.ResultTable;
import com.yz.tools.enums.CodeEnum;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 系统-角色数据表(SysRole)表控制层
 *
 * @author yunze
 * @since 2024-11-17 18:15:15
 */
@RestController
@RequestMapping("sys/role")
public class SysRoleController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysRoleService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysRoleAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysRoleUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 切换角色状态
     *
     * @param id 角色Id
     * @return 是否切换成功
     */
    @PostMapping("switch/{id}")
    public Result<Boolean> switchRole(@PathVariable Long id) {
        boolean updated = service.updateRoleStatusById(id);
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
    @PostMapping("page")
    public Result<ResultTable<SysRole>> page(@RequestBody @Valid PageFilter<SysRoleQueryDto> filter) {
        Page<SysRole> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysRole> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

