package com.yz.unqid.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.web.common.ResultTable;
import com.yz.unqid.dto.InternalUnqidDto;
import com.yz.unqid.dto.SysUnqidAddDto;
import com.yz.unqid.dto.SysUnqidQueryDto;
import com.yz.unqid.dto.SysUnqidUpdateDto;
import com.yz.unqid.entity.SysUnqid;
import com.yz.unqid.service.SysUnqidService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 系统-流水号表(SysUnqid)表控制层
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@RestController
@RequestMapping("/unqid")
public class SysUnqidController extends ApiController {

    @Value("${server.port}")
    private String port;

    /**
     * 服务对象
     */
    @Resource(name = "sysUnqidServiceImpl")
    private SysUnqidService service;

    @Resource(name = "sysUnqidV3ServiceImpl")
    private SysUnqidService v3Service;

    /**
     * 生成流水号
     */
    @PostMapping("generateSerialNumber")
    public Result<String> generateSerialNumber(@RequestBody @Valid InternalUnqidDto dto) {
        System.out.println("节点：" + port);
        return success(this.v3Service.generateSerialNumber(dto.getPrefix(), dto.getNumberLength()));
    }

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
    public Result<ResultTable<SysUnqid>> page(@RequestBody @Valid PageFilter<SysUnqidQueryDto> filter) {
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

