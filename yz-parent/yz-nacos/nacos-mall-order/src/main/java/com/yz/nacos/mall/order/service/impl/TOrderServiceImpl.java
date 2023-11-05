package com.yz.nacos.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.nacos.mall.order.mapper.TOrderMapper;
import com.yz.nacos.mall.order.entity.TOrder;
import com.yz.nacos.mall.order.service.TOrderService;
import org.springframework.stereotype.Service;

/**
 * 订单信息(TOrder)表服务实现类
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@Service("tOrderService")
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements TOrderService {

}

