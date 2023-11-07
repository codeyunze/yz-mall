package com.yz.nacos.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.nacos.mall.product.entity.TProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品信息(TProduct)表数据库访问层
 *
 * @author yunze
 * @since 2023-11-07 23:47:54
 */
@Mapper
public interface TProductMapper extends BaseMapper<TProduct> {

}

