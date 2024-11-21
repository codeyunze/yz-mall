package com.yz.mall.sys.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysOrgAddDto;
import com.yz.mall.sys.dto.SysOrgQueryDto;
import com.yz.mall.sys.dto.SysOrgUpdateDto;
import com.yz.mall.sys.entity.SysOrg;
import com.yz.mall.sys.service.SysOrgService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 系统-组织表(SysOrg)表控制层
 *
 * @author yunze
 * @since 2024-11-17 20:19:07
 */
@RestController
@RequestMapping("sys/org")
public class SysOrgController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysOrgService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysOrgAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysOrgUpdateDto dto) {
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
    public Result<ResultTable<SysOrg>> page(@RequestBody @Valid PageFilter<SysOrgQueryDto> filter) {
        Page<SysOrg> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysOrg> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

