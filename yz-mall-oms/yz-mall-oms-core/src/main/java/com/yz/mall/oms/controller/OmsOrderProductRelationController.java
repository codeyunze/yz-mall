package com.yz.mall.oms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.oms.dto.OmsOrderRelationProductAddDto;
import com.yz.mall.oms.dto.OmsOrderProductRelationQueryDto;
import com.yz.mall.oms.dto.OmsOrderRelationProductUpdateDto;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import com.yz.mall.oms.service.OmsOrderProductRelationService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 订单商品关联表(OmsOrderProductRelation)表控制层
 *
 * @author yunze
 * @since 2024-06-18 12:51:39
 */
@RestController
@RequestMapping("oms/orderProductRelation")
public class OmsOrderProductRelationController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private OmsOrderProductRelationService service;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid OmsOrderRelationProductAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid OmsOrderRelationProductUpdateDto dto) {
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
    public Result<ResultTable<OmsOrderRelationProduct>> page(@RequestBody @Valid PageFilter<OmsOrderProductRelationQueryDto> filter) {
        Page<OmsOrderRelationProduct> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<OmsOrderRelationProduct> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

