package com.yz.mall.seata.tcc.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.yz.mall.seata.tcc.order.dto.TccOrderDto;
import com.yz.mall.seata.tcc.order.entity.TccOrder;
import com.yz.mall.seata.tcc.order.feign.TccAccountFeignService;
import com.yz.mall.seata.tcc.order.feign.TccStorageFeignService;
import com.yz.mall.seata.tcc.order.service.BussinessService;
import com.yz.mall.seata.tcc.order.service.TccOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yunze
 * @date 2024/1/15 12:45
 */
@Service
public class BusinessServiceImpl implements BussinessService {

    @Autowired
    private TccOrderService orderService;

    @Autowired
    private TccStorageFeignService stockFeignService;

    @Autowired
    private TccAccountFeignService accountFeignService;

    @GlobalTransactional(name = "addOrder", rollbackFor = Exception.class)
    @Override
    public TccOrder saveOrder(TccOrderDto order) {
        // 新增订单信息
        TccOrder tccOrder = orderService.prepareSaveOrder(order, IdUtil.getSnowflake().nextId());

        // 扣减库存
        stockFeignService.deduct(order.getProductId(), order.getNum());

        // 扣减账户金额
        accountFeignService.deduct(order.getAccountId(), order.getMoney());

        return tccOrder;
    }
}
