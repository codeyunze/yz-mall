package com.yz.seata.mall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.seata.mall.order.entity.TOrder;
import com.yz.seata.mall.order.feign.TAccountFeignService;
import com.yz.seata.mall.order.feign.TStorageFeignService;
import com.yz.seata.mall.order.mapper.TOrderMapper;
import com.yz.seata.mall.order.service.TOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 订单信息(TOrder)表服务实现类
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@Slf4j
@Service("tOrderService")
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements TOrderService {

    @Autowired
    private TStorageFeignService stockFeignService;

    @Autowired
    private TAccountFeignService accountFeignService;

    /**
     * 单纯使用@Transactional注解存在如下问题：
     * 如果订单新增成功，库存扣减成功，但扣减余额的时候失败了，则扣减的余额会回滚，新增的订单信息会回滚，但是已经扣减的库存却不会再回滚
     */
    // @Transactional
    @GlobalTransactional(name = "addOrder", rollbackFor = Exception.class)
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

