package com.yz.mall.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.pms.entity.PmsProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品表(PmsProduct)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Mapper
public interface PmsProductMapper extends BaseMapper<PmsProduct> {

}

