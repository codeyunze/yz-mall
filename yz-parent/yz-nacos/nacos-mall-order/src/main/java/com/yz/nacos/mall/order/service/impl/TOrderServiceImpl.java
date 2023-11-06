package com.yz.nacos.mall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.nacos.mall.order.entity.TOrder;
import com.yz.nacos.mall.order.feign.TStockFeignService;
import com.yz.nacos.mall.order.mapper.TOrderMapper;
import com.yz.nacos.mall.order.service.TOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单信息(TOrder)表服务实现类
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@Service("tOrderService")
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements TOrderService {

    @Autowired
    private TStockFeignService stockFeignService;

    @Transactional
    @Override
    public TOrder saveOrder(TOrder tOrder) {
        tOrder.setId(IdUtil.getSnowflakeNextId());

        // 新增订单信息
        baseMapper.insert(tOrder);

        stockFeignService.deduct(tOrder.getProductId(), tOrder.getNum());

        return tOrder;
    }
}

