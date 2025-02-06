package com.yz.mall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.oms.dto.InternalOmsOrderByCartDto;
import com.yz.mall.oms.dto.InternalOmsOrderDto;
import com.yz.mall.oms.entity.OmsOrder;

/**
 * 订单信息表(OmsOrder)表服务接口
 *
 * @author yunze
 * @since 2025-01-30 19:12:59
 */
public interface OmsOrderService extends IService<OmsOrder> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 订单主键Id {@link OmsOrder#getId()}
     */
    Long generateOrder(InternalOmsOrderByCartDto dto);

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 订单主键Id {@link OmsOrder#getId()}
     */
    Long add(InternalOmsOrderDto dto);

}

