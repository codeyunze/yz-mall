package com.yz.mall.sys.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysUserRelationOrgAddDto;
import com.yz.mall.sys.dto.SysUserRelationOrgQueryDto;
import com.yz.mall.sys.dto.SysUserRelationOrgUpdateDto;
import com.yz.mall.sys.entity.SysUserRelationOrg;
import com.yz.mall.sys.service.SysUserRelationOrgService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import com.yz.mall.web.enums.CodeEnum;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 系统-用户关联组织数据表(SysUserRelationOrg)表控制层
 *
 * @author yunze
 * @since 2024-11-17 20:26:16
 */
@RestController
@RequestMapping("sys/user/org")
public class SysUserRelationOrgController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysUserRelationOrgService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysUserRelationOrgAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysUserRelationOrgUpdateDto dto) {
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
    public Result<ResultTable<SysUserRelationOrg>> page(@RequestBody @Valid PageFilter<SysUserRelationOrgQueryDto> filter) {
        Page<SysUserRelationOrg> page = this.service.page(filter);
        return new Result<>(CodeEnum.SUCCESS.get(), new ResultTable<>(page.getRecords(), page.getTotal()), "查询成功");
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysUserRelationOrg> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

