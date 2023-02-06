package com.yz.openinterface.business.shop.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.openinterface.business.shop.stock.dao.ShopStockDao;
import com.yz.openinterface.business.shop.stock.entity.ShopStock;
import com.yz.openinterface.business.shop.stock.service.ShopStockService;
import org.springframework.stereotype.Service;

/**
 * 库存表(ShopStock)表服务实现类
 *
 * @author makejava
 * @since 2023-02-06 22:44:54
 */
@Service("shopStockService")
public class ShopStockServiceImpl extends ServiceImpl<ShopStockDao, ShopStock> implements ShopStockService {

}

