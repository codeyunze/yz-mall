package com.yz.dynamic.datasource.one.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.dynamic.datasource.one.dto.StockAddDto;
import com.yz.dynamic.datasource.one.entity.TStock;

/**
 * 库存信息(TStock)表服务接口
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
public interface TStockService extends IService<TStock> {

    Integer save(StockAddDto dto);

}

