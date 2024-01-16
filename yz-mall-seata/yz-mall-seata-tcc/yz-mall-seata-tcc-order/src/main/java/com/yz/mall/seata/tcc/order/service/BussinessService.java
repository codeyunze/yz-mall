package com.yz.mall.seata.tcc.order.service;

import com.yz.mall.seata.tcc.order.dto.TccOrderDto;
import com.yz.mall.seata.tcc.order.entity.TccOrder;

/**
 * 业务接口
 * @author yunze
 * @date 2024/1/15 12:42
 */
public interface BussinessService {

    /**
     * 新增订单信息
     * @param order 订单基础信息
     * @return 订单信息
     */
    TccOrder saveOrder(TccOrderDto order);
}
