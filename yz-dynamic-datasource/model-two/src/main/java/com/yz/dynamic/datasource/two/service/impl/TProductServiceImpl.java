package com.yz.dynamic.datasource.two.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.dynamic.datasource.two.mapper.TProductMapper;
import com.yz.dynamic.datasource.two.entity.TProduct;
import com.yz.dynamic.datasource.two.service.TProductService;
import org.springframework.stereotype.Service;

/**
 * 商品信息(TProduct)表服务实现类
 *
 * @author yunze
 * @since 2023-10-31 00:00:29
 */
@DS("product")
@Service("tProductService")
public class TProductServiceImpl extends ServiceImpl<TProductMapper, TProduct> implements TProductService {

}

