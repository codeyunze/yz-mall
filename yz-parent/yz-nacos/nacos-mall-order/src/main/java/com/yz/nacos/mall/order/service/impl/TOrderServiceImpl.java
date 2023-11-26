package com.yz.nacos.mall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.nacos.mall.order.entity.TOrder;
import com.yz.nacos.mall.order.feign.TAccountFeignService;
import com.yz.nacos.mall.order.feign.TStorageFeignService;
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
    private TStorageFeignService stockFeignService;

    @Autowired
    private TAccountFeignService accountFeignService;

    /**
     * 如果订单新增成功，库存扣减成功，但扣减余额的时候失败了，则扣减的余额会回滚，新增的订单信息会回滚，但是已经扣减的库存却不会再回滚
     */
    @Transactional
    @Override
    public TOrder saveOrder(TOrder tOrder) {
        tOrder.setId(IdUtil.getSnowflakeNextId());

        // 新增订单信息
        baseMapper.insert(tOrder);

        // 扣减库存
        stockFeignService.deduct(tOrder.getProductId(), tOrder.getNum());

        // 扣减余额
        accountFeignService.deduct(tOrder.getAccountId(), tOrder.getAmount());

        return tOrder;
    }
}

