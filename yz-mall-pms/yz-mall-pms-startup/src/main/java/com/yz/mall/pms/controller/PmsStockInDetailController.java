package com.yz.mall.pms.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsStockInDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockInDetailUpdateDto;
import com.yz.mall.pms.entity.PmsStockInDetail;
import com.yz.mall.pms.service.PmsStockInDetailService;
import com.yz.mall.pms.vo.PmsStockInDetailVo;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表控制层
 *
 * @author yunze
 * @since 2024-12-25 19:53:27
 */
@RestController
@RequestMapping("pms/stock/in")
public class PmsStockInDetailController extends ApiController {

    private final PmsStockInDetailService service;

    public PmsStockInDetailController(PmsStockInDetailService service) {
        this.service = service;
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
    @SaCheckPermission("api:pms:stock:in:page")
    @PostMapping("page")
    public Result<ResultTable<PmsStockInDetailVo>> page(@RequestBody @Valid PageFilter<PmsStockInDetailQueryDto> filter) {
        Page<PmsStockInDetailVo> page = this.service.page(filter);
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

