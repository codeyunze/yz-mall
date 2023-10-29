package com.yz.dynamic.datasource.one.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.dynamic.datasource.one.dto.ProductAddDto;
import com.yz.dynamic.datasource.one.entity.TProduct;

/**
 * 商品信息(TProduct)表服务接口
 *
 * @author yunze
 * @since 2023-10-29 18:01:13
 */
public interface TProductService extends IService<TProduct> {

    Integer save(ProductAddDto dto);
}

