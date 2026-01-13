package com.yz.mall.pms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsSkuAddDto;
import com.yz.mall.pms.dto.PmsSkuQueryDto;
import com.yz.mall.pms.dto.PmsSkuUpdateDto;
import com.yz.mall.pms.entity.PmsSku;
import com.yz.mall.pms.service.PmsSkuService;
import com.yz.mall.pms.vo.PmsSkuVo;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 商品SKU表(PmsSku)表控制层
 *
 * @author yunze
 * @since 2025-01-XX
 */
@RestController
@RequestMapping("pms/sku")
public class PmsSkuController extends ApiController {

    private final PmsSkuService service;

    public PmsSkuController(PmsSkuService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @SaCheckPermission("api:pms:sku:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid PmsSkuAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @SaCheckPermission("api:pms:sku:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid PmsSkuUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键 ID
     */
    @SaCheckPermission("api:pms:sku:edit")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:pms:sku:page")
    @PostMapping("page")
    public Result<ResultTable<PmsSku>> page(@RequestBody @Valid PageFilter<PmsSkuQueryDto> filter) {
        Page<PmsSku> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("api:pms:sku:page")
    @GetMapping("get/{id}")
    public Result<PmsSkuVo> get(@PathVariable Long id) {
        return success(this.service.detail(id));
    }

    /**
     * 根据商品ID查询SKU列表
     */
    @SaCheckPermission("api:pms:sku:page")
    @GetMapping("list/{productId}")
    public Result<List<PmsSkuVo>> listByProductId(@PathVariable Long productId) {
        return success(this.service.listByProductId(productId));
    }

}
