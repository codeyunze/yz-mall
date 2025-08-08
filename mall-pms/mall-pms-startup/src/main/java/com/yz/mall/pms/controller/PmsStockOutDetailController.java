package com.yz.mall.pms.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsStockOutDetailQueryDto;
import com.yz.mall.pms.entity.PmsStockOutDetail;
import com.yz.mall.pms.service.PmsStockOutDetailService;
import com.yz.mall.pms.vo.PmsStockOutDetailVo;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 产品管理-商品出库日志表(PmsStockOutDetail)表控制层
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
@RestController
@RequestMapping("pms/stock/out")
public class PmsStockOutDetailController extends ApiController {

    /**
     * 服务对象
     */
    private final PmsStockOutDetailService service;

    public PmsStockOutDetailController(PmsStockOutDetailService service) {
        this.service = service;
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:pms:stock:out:page")
    @PostMapping("page")
    public Result<ResultTable<PmsStockOutDetailVo>> page(@RequestBody @Valid PageFilter<PmsStockOutDetailQueryDto> filter) {
        Page<PmsStockOutDetailVo> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("api:pms:stock:out:get")
    @GetMapping("get/{id}")
    public Result<PmsStockOutDetail> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

