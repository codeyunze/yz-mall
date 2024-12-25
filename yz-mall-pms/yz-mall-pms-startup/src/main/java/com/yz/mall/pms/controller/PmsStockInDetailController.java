package com.yz.mall.pms.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsStockInDetailAddDto;
import com.yz.mall.pms.dto.PmsStockInDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockInDetailUpdateDto;
import com.yz.mall.pms.entity.PmsStockInDetail;
import com.yz.mall.pms.service.PmsStockInDetailService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表控制层
 *
 * @author yunze
 * @since 2024-12-25 19:53:27
 */
@RestController
@RequestMapping("pms/stock/detail")
public class PmsStockInDetailController extends ApiController {

    private final PmsStockInDetailService service;

    public PmsStockInDetailController(PmsStockInDetailService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @SaCheckPermission("api:pms:stock:detail:add")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid PmsStockInDetailAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid PmsStockInDetailUpdateDto dto) {
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
    public Result<ResultTable<PmsStockInDetail>> page(@RequestBody @Valid PageFilter<PmsStockInDetailQueryDto> filter) {
        Page<PmsStockInDetail> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<PmsStockInDetail> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

