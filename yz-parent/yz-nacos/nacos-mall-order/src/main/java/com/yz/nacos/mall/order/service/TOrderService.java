package com.yz.nacos.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.nacos.mall.order.entity.TOrder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 订单信息(TOrder)表服务接口
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
public interface TOrderService extends IService<TOrder> {

    TOrder saveOrder(TOrder tOrder);
}

