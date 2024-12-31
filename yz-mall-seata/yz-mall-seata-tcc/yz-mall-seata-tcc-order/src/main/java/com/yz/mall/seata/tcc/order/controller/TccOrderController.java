package com.yz.mall.seata.tcc.order.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.seata.tcc.order.dto.TccOrderDto;
import com.yz.mall.seata.tcc.order.entity.TccOrder;
import com.yz.mall.seata.tcc.order.service.BussinessService;
import com.yz.mall.seata.tcc.order.service.TccOrderService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.TableResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 订单信息(TOrder)表控制层
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@RestController
@RequestMapping("order")
public class TccOrderController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private TccOrderService tOrderService;

    @Resource
    private BussinessService bussinessService;

    /**
     * 分页查询所有数据
     *
     * @param filter 分页查询过滤条件
     * @return 所有数据
     */
    @PostMapping("/page")
    public TableResult<List<TccOrder>> page(@RequestBody @Validated PageFilter<TccOrder> filter) {
        Page<TccOrder> page = this.tOrderService.page(new Page<>(filter.getCurrent(), filter.getSize()), new QueryWrapper<>(filter.getFilter()));
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/get/{id}")
    public Result<TccOrder> selectOne(@PathVariable Serializable id) {
        return success(this.tOrderService.getById(id));
    }

    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Result<TccOrder> insert(@RequestBody @Validated TccOrderDto dto) {
        return success(this.bussinessService.saveOrder(dto));
    }

    /**
     * 修改数据
     *
     * @param tOrder 实体对象
     * @return 修改结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody @Validated TccOrder tOrder) {
        return success(this.tOrderService.updateById(tOrder));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.tOrderService.removeById(id));
    }
}

