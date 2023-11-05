package com.yz.nacos.mall.order.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.nacos.mall.order.entity.TOrder;
import com.yz.nacos.mall.order.service.TOrderService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.TableResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 订单信息(TOrder)表控制层
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@RestController
@RequestMapping("tOrder")
public class TOrderController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private TOrderService tOrderService;

    /**
     * 分页查询所有数据
     *
     * @param filter 分页查询过滤条件
     * @return 所有数据
     */
    @PostMapping("/page")
    public TableResult<List<TOrder>> page(@RequestBody @Validated PageFilter<TOrder> filter) {
        Page<TOrder> page = this.tOrderService.page(new Page<>(filter.getCurrent(), filter.getSize()), new QueryWrapper<>(filter.getFilter()));
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/get/{id}")
    public Result<TOrder> selectOne(@PathVariable Serializable id) {
        return success(this.tOrderService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tOrder 实体对象
     * @return 新增结果
     */
    @PostMapping("/add")
    public Result<Boolean> insert(@RequestBody @Validated TOrder tOrder) {
        return success(this.tOrderService.save(tOrder));
    }

    /**
     * 修改数据
     *
     * @param tOrder 实体对象
     * @return 修改结果
     */
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody @Validated TOrder tOrder) {
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

