package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysReceiptInfoAddDto;
import com.yz.mall.sys.dto.SysReceiptInfoQueryDto;
import com.yz.mall.sys.dto.SysReceiptInfoUpdateDto;
import com.yz.mall.sys.entity.SysReceiptInfo;
import com.yz.mall.sys.service.SysReceiptInfoService;
import com.yz.mall.sys.vo.SysReceiptInfoVo;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 系统-用户收货信息(SysReceiptInfo)表控制层
 *
 * @author yunze
 * @since 2025-03-03 15:40:27
 */
@RestController
@RequestMapping("sys/receipt")
public class SysReceiptInfoController extends ApiController {

    /**
     * 服务对象
     */
    private final SysReceiptInfoService service;

    public SysReceiptInfoController(SysReceiptInfoService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @SaCheckPermission("api:system:receipt:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysReceiptInfoAddDto dto) {
        dto.setCreateId(StpUtil.getLoginIdAsLong());
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @SaCheckPermission("api:system:receipt:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysReceiptInfoUpdateDto dto) {
        dto.setCreateId(StpUtil.getLoginIdAsLong());
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @SaCheckPermission("api:system:receipt:edit")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id, StpUtil.getLoginIdAsLong()));
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:system:receipt:edit")
    @PostMapping("page")
    public Result<ResultTable<SysReceiptInfoVo>> page(@RequestBody @Valid PageFilter<SysReceiptInfoQueryDto> filter) {
        filter.getFilter().setCreateId(StpUtil.getLoginIdAsLong());
        Page<SysReceiptInfoVo> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }
}

