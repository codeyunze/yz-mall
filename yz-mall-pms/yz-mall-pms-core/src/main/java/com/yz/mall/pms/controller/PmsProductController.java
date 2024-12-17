package com.yz.mall.pms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.entity.PmsProduct;
import com.yz.mall.pms.service.PmsProductService;
import com.yz.mall.pms.dto.PmsProductAddDto;
import com.yz.mall.pms.dto.PmsProductQueryDto;
import com.yz.mall.pms.dto.PmsProductUpdateDto;
import com.yz.mall.pms.vo.PmsProductVo;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 商品表(PmsProduct)表控制层
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@RestController
@RequestMapping("mall/product")
public class PmsProductController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private PmsProductService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid PmsProductAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid PmsProductUpdateDto dto) {
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
    public Result<ResultTable<PmsProduct>> page(@RequestBody @Valid PageFilter<PmsProductQueryDto> filter) {
        Page<PmsProduct> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<PmsProductVo> page(@PathVariable String id) {
        return success(this.service.detail(id));
    }

}

