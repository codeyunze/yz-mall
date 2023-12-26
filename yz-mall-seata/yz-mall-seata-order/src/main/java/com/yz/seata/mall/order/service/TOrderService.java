package com.yz.seata.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.seata.mall.order.entity.TOrder;

/**
 * 订单信息(TOrder)表服务接口
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
public interface TOrderService extends IService<TOrder> {

    TOrder saveOrder(TOrder tOrder);
}

