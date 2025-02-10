package com.yz.mall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import com.yz.mall.oms.vo.OmsOrderProductVo;

import java.util.List;
import java.util.Map;

/**
 * 订单商品关联表(OmsOrderRelationProduct)表服务接口
 *
 * @author yunze
 * @since 2025-01-30 19:14:03
 */
public interface OmsOrderRelationProductService extends IService<OmsOrderRelationProduct> {

    /**
     * 根据订单Id查询订单商品信息
     *
     * @param orderIds 订单Id列表
     * @return 订单商品信息 Map<订单Id, 订单的商品信息>
     */
    Map<Long, List<OmsOrderProductVo>> getOrderProductByOrderIds(List<Long> orderIds);
}

