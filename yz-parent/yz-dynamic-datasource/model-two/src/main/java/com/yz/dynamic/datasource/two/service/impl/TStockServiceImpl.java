package com.yz.dynamic.datasource.two.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.dynamic.datasource.two.mapper.TStockMapper;
import com.yz.dynamic.datasource.two.entity.TStock;
import com.yz.dynamic.datasource.two.service.TStockService;
import com.yz.tools.enums.DataSourceTypeEnum;
import org.springframework.stereotype.Service;

/**
 * 库存信息(TStock)表服务实现类
 *
 * @author yunze
 * @since 2023-10-31 00:02:31
 */
@DS("stock")
@Service("tStockService")
public class TStockServiceImpl extends ServiceImpl<TStockMapper, TStock> implements TStockService {

}

