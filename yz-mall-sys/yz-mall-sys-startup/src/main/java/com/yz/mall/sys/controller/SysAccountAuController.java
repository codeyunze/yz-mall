package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysAccountAuAddDto;
import com.yz.mall.sys.dto.SysAccountAuChooseQueryDto;
import com.yz.mall.sys.dto.SysAccountAuQueryDto;
import com.yz.mall.sys.dto.SysAccountAuUpdateDto;
import com.yz.mall.sys.entity.SysAccountAu;
import com.yz.mall.sys.service.SysAccountAuService;
import com.yz.mall.sys.vo.SysAccountAuChooseVo;
import com.yz.mall.sys.vo.SysAccountAuVo;
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
    @SaCheckPermission("api:system:account:au:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SysAccountAuAddDto dto) {
        dto.setUserId(Long.parseLong(StpUtil.getLoginId().toString()));
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @SaCheckPermission("api:system:account:au:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysAccountAuUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @SaCheckPermission("api:system:account:au:edit")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:system:account:au:edit")
    @PostMapping("page")
    public Result<ResultTable<SysAccountAuVo>> page(@RequestBody @Valid PageFilter<SysAccountAuQueryDto> filter) {
        Page<SysAccountAuVo> page = this.service.getPageByFilter(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 分页查询交易汇总信息
     */
    @SaCheckPermission("api:system:account:au:edit")
    @PostMapping("pageSummary")
    public Result<ResultTable<SysAccountAuVo>> pageSummary(@RequestBody @Valid PageFilter<SysAccountAuQueryDto> filter) {
        Page<SysAccountAuVo> page = this.service.getPageSummaryByFilter(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 分页查询还有剩余的买入记录
     */
    @SaCheckPermission("api:system:account:au:edit")
    @PostMapping("choose")
    public Result<ResultTable<SysAccountAuChooseVo>> choose(@RequestBody @Valid PageFilter<SysAccountAuChooseQueryDto> filter) {
        Page<SysAccountAuChooseVo> page = this.service.getChooseByFilter(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("api:system:account:au:edit")
    @GetMapping("get/{id}")
    public Result<SysAccountAu> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

