package com.yz.openinterface.business.shop.order.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.openinterface.business.shop.order.entity.ShopOrder;

import java.util.concurrent.Future;

/**
 * 订单表(ShopOrder)表服务接口
 *
 * @author yunze
 * @since 2023-02-06 23:45:11
 */
public interface ShopOrderService extends IService<ShopOrder> {

    Future<Page<ShopOrder>> selectAllPage(Page page, QueryWrapper query);
}

