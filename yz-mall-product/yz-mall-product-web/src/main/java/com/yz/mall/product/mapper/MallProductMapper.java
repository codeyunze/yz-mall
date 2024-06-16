package com.yz.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.product.entity.MallProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品表(MallProduct)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Mapper
public interface MallProductMapper extends BaseMapper<MallProduct> {

}

