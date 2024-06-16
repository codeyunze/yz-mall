package com.yz.mall.product.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.product.dto.MallProductAddDto;
import com.yz.mall.product.dto.MallProductQueryDto;
import com.yz.mall.product.dto.MallProductUpdateDto;
import com.yz.mall.product.entity.MallProduct;
import com.yz.mall.product.service.MallProductService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 商品表(MallProduct)表控制层
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@RestController
@RequestMapping("mall/product")
public class MallProductController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private MallProductService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid MallProductAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid MallProductUpdateDto dto) {
        return success(this.service.update(dto));
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
    public Result<List<MallProduct>> page(@RequestBody @Valid PageFilter<MallProductQueryDto> filter) {
        Page<MallProduct> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<MallProduct> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

