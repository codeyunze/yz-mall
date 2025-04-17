package com.yz.mall.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import com.yz.mall.oms.mapper.OmsOrderRelationProductMapper;
import com.yz.mall.oms.service.OmsOrderRelationProductService;
import com.yz.mall.oms.vo.OmsOrderProductVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单商品关联表(OmsOrderRelationProduct)表服务实现类
 *
 * @author yunze
 * @since 2025-01-30 19:14:03
 */
@Service
public class OmsOrderRelationProductServiceImpl extends ServiceImpl<OmsOrderRelationProductMapper, OmsOrderRelationProduct> implements OmsOrderRelationProductService {

    @Override
    public Map<Long, List<OmsOrderProductVo>> getOrderProductsByOrderIds(List<Long> orderIds) {
        List<OmsOrderProductVo> productVos = baseMapper.selectOrderProductByOrderIds(orderIds);
        if (CollectionUtils.isEmpty(productVos)) {
            return null;
        }
        return productVos.stream().collect(Collectors.groupingBy(OmsOrderProductVo::getOrderId));
    }

    @Override
    public List<OmsOrderProductVo> getOrderProductsByOrderId(Long orderId) {
        return baseMapper.selectOrderProductByOrderIds(Collections.singletonList(orderId));
    }
}

