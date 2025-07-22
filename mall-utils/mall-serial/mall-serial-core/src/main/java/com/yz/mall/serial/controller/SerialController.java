package com.yz.mall.serial.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.serial.dto.ExtendUnqidDto;
import com.yz.mall.serial.dto.SerialAddDto;
import com.yz.mall.serial.dto.SerialQueryDto;
import com.yz.mall.serial.dto.SerialUpdateDto;
import com.yz.mall.serial.entity.SysUnqid;
import com.yz.mall.serial.service.SerialService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 工具-流水号管理
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@RestController
@RequestMapping("/unqid")
public class SerialController extends ApiController {

    @Value("${server.port}")
    private String port;

    /**
     * 服务对象
     */
    @Resource(name = "serialServiceImpl")
    private SerialService service;

    @Resource(name = "serialPoolServiceImpl")
    private SerialService v3Service;

    /**
     * 生成流水号
     */
    @PostMapping("generateNumber")
    public Result<String> generateNumber(@RequestBody @Valid ExtendUnqidDto dto) {
        System.out.println("节点：" + port);
        return success(this.v3Service.generateSerialNumber(dto.getPrefix(), dto.getNumberLength()));
    }

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid SerialAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SerialUpdateDto dto) {
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
    public Result<ResultTable<SysUnqid>> page(@RequestBody @Valid PageFilter<SerialQueryDto> filter) {
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

