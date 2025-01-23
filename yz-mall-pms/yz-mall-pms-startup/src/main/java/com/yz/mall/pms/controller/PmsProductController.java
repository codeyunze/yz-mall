package com.yz.mall.pms.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsProductAddDto;
import com.yz.mall.pms.dto.PmsProductQueryDto;
import com.yz.mall.pms.dto.PmsProductSlimQueryDto;
import com.yz.mall.pms.dto.PmsProductUpdateDto;
import com.yz.mall.pms.entity.PmsProduct;
import com.yz.mall.pms.service.PmsProductService;
import com.yz.mall.pms.vo.PmsProductDisplayInfoVo;
import com.yz.mall.pms.vo.PmsProductVo;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品表(PmsProduct)表控制层
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@RestController
@RequestMapping("pms/product")
public class PmsProductController extends ApiController {

    private final PmsProductService service;

    public PmsProductController(PmsProductService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @SaCheckPermission("api:pms:product:edit")
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid PmsProductAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @SaCheckPermission("api:pms:product:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid PmsProductUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 商品上架
     */
    @SaCheckPermission("api:pms:product:edit")
    @PostMapping("publish/{id}")
    public Result<Boolean> publish(@PathVariable Long id) {
        return success(this.service.publish(id));
    }

    /**
     * 商品下架
     */
    @SaCheckPermission("api:pms:product:edit")
    @PostMapping("delisting/{id}")
    public Result<Boolean> delisting(@PathVariable Long id) {
        return success(this.service.delisting(id));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @SaCheckPermission("api:pms:product:edit")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:pms:product:page")
    @PostMapping("page")
    public Result<ResultTable<PmsProduct>> page(@RequestBody @Valid PageFilter<PmsProductQueryDto> filter) {
        Page<PmsProduct> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("api:pms:product:page")
    @GetMapping("get/{id}")
    public Result<PmsProductVo> page(@PathVariable Long id) {
        return success(this.service.detail(id));
    }

    /**
     * 查询商品展示信息
     */
    @SaIgnore
    @PostMapping("/info")
    public Result<List<PmsProductDisplayInfoVo>> getProductDisplayInfoList(@RequestBody PmsProductSlimQueryDto filter) {
        return success(this.service.getProductDisplayInfoList(filter));
    }
}

