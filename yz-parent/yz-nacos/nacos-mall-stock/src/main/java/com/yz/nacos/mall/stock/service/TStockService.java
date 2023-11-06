package com.yz.nacos.mall.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.nacos.mall.stock.entity.TStock;

/**
 * 库存信息(TStock)表服务接口
 *
 * @author yunze
 * @since 2023-11-05 15:59:36
 */
public interface TStockService extends IService<TStock> {

    boolean deduct(Long productId, Integer num);
}

