package com.yz.mall.sys.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysRoleRelationAddDto;
import com.yz.mall.sys.dto.SysRoleRelationQueryDto;
import com.yz.mall.sys.dto.SysRoleRelationUpdateDto;
import com.yz.mall.sys.entity.SysRoleRelation;
import com.yz.mall.sys.service.SysRoleRelationService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 系统-关联角色数据表(SysRoleRelation)表控制层
 *
 * @author yunze
 * @since 2024-11-17 19:55:59
 */
@RestController
@RequestMapping("sys/role/relation")
public class SysRoleRelationController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysRoleRelationService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysRoleRelationAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysRoleRelationUpdateDto dto) {
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
    @PostMapping("page")
    public Result<ResultTable<SysRoleRelation>> page(@RequestBody @Valid PageFilter<SysRoleRelationQueryDto> filter) {
        Page<SysRoleRelation> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysRoleRelation> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

