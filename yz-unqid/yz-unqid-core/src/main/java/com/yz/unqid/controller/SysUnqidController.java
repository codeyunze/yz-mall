package com.yz.unqid.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.unqid.dto.SysUnqidAddDto;
import com.yz.unqid.dto.SysUnqidQueryDto;
import com.yz.unqid.dto.SysUnqidUpdateDto;
import com.yz.unqid.entity.SysUnqid;
import com.yz.unqid.service.SysUnqidService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 系统-流水号表(SysUnqid)表控制层
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@RestController
@RequestMapping("sys/unqid")
public class SysUnqidController extends ApiController {

    /**
     * 服务对象
     */
    @Resource(name = "sysUnqidServiceImpl")
    private SysUnqidService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid SysUnqidAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysUnqidUpdateDto dto) {
        return success(this.service.cachePersistence(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<List<SysUnqid>> page(@RequestBody @Valid PageFilter<SysUnqidQueryDto> filter) {
        Page<SysUnqid> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysUnqid> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

