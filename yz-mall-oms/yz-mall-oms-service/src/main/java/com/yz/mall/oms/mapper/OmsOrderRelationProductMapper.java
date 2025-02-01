package com.yz.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单商品关联表(OmsOrderRelationProduct)表数据库访问层
 *
 * @author yunze
 * @since 2025-01-30 19:14:03
 */
@Mapper
public interface OmsOrderRelationProductMapper extends BaseMapper<OmsOrderRelationProduct> {

}

