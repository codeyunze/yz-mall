package com.yz.mall.pms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsStockOutDetailAddDto;
import com.yz.mall.pms.dto.PmsStockOutDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockOutDetailUpdateDto;
import com.yz.mall.pms.entity.PmsStockOutDetail;
import com.yz.mall.pms.service.PmsStockOutDetailService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 产品管理-商品出库日志表(PmsStockOutDetail)表控制层
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
@RestController
@RequestMapping("pmsStockOutDetail")
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
    @SaCheckPermission("pms:stock:out:detail:page")
    @PostMapping("page")
    public Result<ResultTable<PmsStockOutDetail>> page(@RequestBody @Valid PageFilter<PmsStockOutDetailQueryDto> filter) {
        Page<PmsStockOutDetail> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("pms:stock:out:detail:get")
    @GetMapping("get/{id}")
    public Result<PmsStockOutDetail> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

