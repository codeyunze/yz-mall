package com.yz.nacos.mall.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.nacos.mall.stock.mapper.TStockMapper;
import com.yz.nacos.mall.stock.entity.TStock;
import com.yz.nacos.mall.stock.service.TStockService;
import org.springframework.stereotype.Service;

/**
 * 库存信息(TStock)表服务实现类
 *
 * @author yunze
 * @since 2023-11-05 15:59:36
 */
@Service("tStockService")
public class TStockServiceImpl extends ServiceImpl<TStockMapper, TStock> implements TStockService {

}

