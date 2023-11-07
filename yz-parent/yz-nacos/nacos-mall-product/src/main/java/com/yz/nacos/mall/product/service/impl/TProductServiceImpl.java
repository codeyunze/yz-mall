package com.yz.nacos.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.nacos.mall.product.mapper.TProductMapper;
import com.yz.nacos.mall.product.entity.TProduct;
import com.yz.nacos.mall.product.service.TProductService;
import org.springframework.stereotype.Service;

/**
 * 商品信息(TProduct)表服务实现类
 *
 * @author yunze
 * @since 2023-11-07 23:47:54
 */
@Service("tProductService")
public class TProductServiceImpl extends ServiceImpl<TProductMapper, TProduct> implements TProductService {

}

