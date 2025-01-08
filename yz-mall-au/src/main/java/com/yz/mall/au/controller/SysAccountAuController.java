package com.yz.mall.au.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.au.dto.SysAccountAuAddDto;
import com.yz.mall.au.dto.SysAccountAuQueryDto;
import com.yz.mall.au.dto.SysAccountAuUpdateDto;
import com.yz.mall.au.entity.SysAccountAu;
import com.yz.mall.au.service.SysAccountAuService;
import com.yz.mall.au.vo.SysAccountAuVo;
import com.yz.mall.web.annotation.RepeatSubmit;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 个人黄金账户(SysAccountAu)表控制层
 *
 * @author yunze
 * @since 2025-01-05 10:06:32
 */
@RestController
@RequestMapping("sys/account/au")
public class SysAccountAuController extends ApiController {

    /**
     * 服务对象
     */
    private final SysAccountAuService service;

    public SysAccountAuController(SysAccountAuService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @RepeatSubmit
    @SaCheckPermission("api:sys:account:au:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysAccountAuAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @SaCheckPermission("api:sys:account:au:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysAccountAuUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @SaCheckPermission("api:sys:account:au:edit")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:sys:account:au:edit")
    @PostMapping("page")
    public Result<ResultTable<SysAccountAuVo>> page(@RequestBody @Valid PageFilter<SysAccountAuQueryDto> filter) {
        Page<SysAccountAuVo> page = this.service.getPageByFilter(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("api:sys:account:au:edit")
    @GetMapping("get/{id}")
    public Result<SysAccountAu> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

