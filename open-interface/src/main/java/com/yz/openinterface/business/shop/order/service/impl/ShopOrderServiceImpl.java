package com.yz.openinterface.business.shop.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.openinterface.business.shop.order.dao.ShopOrderDao;
import com.yz.openinterface.business.shop.order.entity.ShopOrder;
import com.yz.openinterface.business.shop.order.service.ShopOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * 订单表(ShopOrder)表服务实现类
 *
 * @author makejava
 * @since 2023-02-06 23:45:11
 */
@Service("shopOrderService")
@Slf4j
public class ShopOrderServiceImpl extends ServiceImpl<ShopOrderDao, ShopOrder> implements ShopOrderService {

    @Async("asyncServiceExecutor")
    @Override
    public Future<Page<ShopOrder>> selectAllPage(Page page, QueryWrapper query) {
        Page<ShopOrder> orderPage = this.page(page, query);
        log.error("当前执行线程名称： {}", Thread.currentThread().getName());
        return AsyncResult.forValue(orderPage);
    }
}

