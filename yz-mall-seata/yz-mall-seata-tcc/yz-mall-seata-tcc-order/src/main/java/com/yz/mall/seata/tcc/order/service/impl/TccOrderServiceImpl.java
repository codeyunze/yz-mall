package com.yz.mall.seata.tcc.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.seata.tcc.order.dto.TccOrderDto;
import com.yz.mall.seata.tcc.order.entity.OrderStatus;
import com.yz.mall.seata.tcc.order.entity.TccOrder;
import com.yz.mall.seata.tcc.order.mapper.TccOrderMapper;
import com.yz.mall.seata.tcc.order.service.TccOrderService;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 订单信息(TOrder)表服务实现类
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@Slf4j
@Service
public class TccOrderServiceImpl extends ServiceImpl<TccOrderMapper, TccOrder> implements TccOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TccOrder prepareSaveOrder(TccOrderDto order, Long orderId) {
        TccOrder bo = new TccOrder();
        BeanUtils.copyProperties(order, bo);
        bo.setId(orderId);
        bo.setState(OrderStatus.INIT.getValue());
        int inserted = baseMapper.insert(bo);
        log.info("保存订单{}", inserted > 0 ? "成功" : "失败");
        return bo;
    }

    @Override
    public boolean commit(BusinessActionContext actionContext) {
        Long orderId = Long.parseLong(Objects.requireNonNull(actionContext.getActionContext("orderId")).toString());
        // 更新订单状态为支付成功
        return baseMapper.updateOrderStatus(orderId, OrderStatus.SUCCESS.getValue()) > 0;
    }

    @Override
    public boolean rollback(BusinessActionContext actionContext) {
        Long orderId = Long.parseLong(Objects.requireNonNull(actionContext.getActionContext("orderId")).toString());
        // 更新订单状态为支付失败
        return baseMapper.updateOrderStatus(orderId, OrderStatus.FAIL.getValue()) > 0;
    }
}

