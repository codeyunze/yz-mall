package com.yz.mall.oms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.oms.dto.OmsOrderAddDto;
import com.yz.mall.oms.dto.OmsOrderGenerateDto;
import com.yz.mall.oms.dto.OmsOrderQueryDto;
import com.yz.mall.oms.dto.OmsOrderUpdateDto;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.mall.oms.service.OmsOrderService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 订单信息表(OmsOrder)表控制层
 *
 * @author yunze
 * @since 2024-06-18 12:49:54
 */
@RestController
@RequestMapping("omsOrder")
public class OmsOrderController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private OmsOrderService service;

    /**
     * 生成订单
     */
    @PostMapping("generateOrder")
    public Result<String> generateOrder(@RequestBody @Valid OmsOrderGenerateDto dto) {
        return success(this.service.generateOrder(dto));
    }


    /**
     * 新增
     */
    @Deprecated
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid OmsOrderAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @Deprecated
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid OmsOrderUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @Deprecated
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<List<OmsOrder>> page(@RequestBody @Valid PageFilter<OmsOrderQueryDto> filter) {
        Page<OmsOrder> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<OmsOrder> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

