package com.yz.openinterface.business.shop.order.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yz.common.extension.api.ApiController;
import com.yz.common.vo.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.openinterface.business.shop.order.entity.ShopOrder;
import com.yz.openinterface.business.shop.order.service.ShopOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 订单表(ShopOrder)表控制层
 *
 * @author makejava
 * @since 2023-02-06 23:45:11
 */
@RestController
@RequestMapping("shopOrder")
public class ShopOrderController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ShopOrderService shopOrderService;

    /**
     * 分页查询所有数据
     *
     * @param page      分页对象
     * @param shopOrder 查询实体
     * @return 所有数据
     */
    @GetMapping
    public Result<Page<ShopOrder>> selectAll(Page<ShopOrder> page, ShopOrder shopOrder) {
        return success(this.shopOrderService.page(page, new QueryWrapper<>(shopOrder)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result<ShopOrder> selectOne(@PathVariable Serializable id) {
        return success(this.shopOrderService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param shopOrder 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result insert(@RequestBody ShopOrder shopOrder) {
        return success(this.shopOrderService.save(shopOrder));
    }

    /**
     * 修改数据
     *
     * @param shopOrder 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result update(@RequestBody ShopOrder shopOrder) {
        return success(this.shopOrderService.updateById(shopOrder));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public Result delete(@RequestParam("idList") List<Long> idList) {
        return success(this.shopOrderService.removeByIds(idList));
    }
}

