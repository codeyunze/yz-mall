package com.yz.openinterface.business.shop.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.openinterface.business.shop.order.dao.ShopOrderDao;
import com.yz.openinterface.business.shop.order.entity.ShopOrder;
import com.yz.openinterface.business.shop.order.service.ShopOrderService;
import org.springframework.stereotype.Service;

/**
 * 订单表(ShopOrder)表服务实现类
 *
 * @author makejava
 * @since 2023-02-06 23:45:11
 */
@Service("shopOrderService")
public class ShopOrderServiceImpl extends ServiceImpl<ShopOrderDao, ShopOrder> implements ShopOrderService {

}

