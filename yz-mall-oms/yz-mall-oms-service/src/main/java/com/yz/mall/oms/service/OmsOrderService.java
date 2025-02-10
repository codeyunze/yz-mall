package com.yz.mall.oms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.oms.dto.InternalOmsOrderByCartDto;
import com.yz.mall.oms.dto.InternalOmsOrderDto;
import com.yz.mall.oms.dto.OmsOrderQueryDto;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.mall.oms.vo.OmsOrderVo;
import com.yz.mall.web.common.PageFilter;

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

    /**
     * 订单信息分页查询
     *
     * @param filter 数据过滤条件
     * @return 订单信息
     */
    Page<OmsOrderVo> page(PageFilter<OmsOrderQueryDto> filter);
}

