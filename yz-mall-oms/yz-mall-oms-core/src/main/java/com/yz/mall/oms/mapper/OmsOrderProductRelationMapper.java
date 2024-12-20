package com.yz.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单商品关联表(OmsOrderProductRelation)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-18 12:51:39
 */
@Mapper
public interface OmsOrderProductRelationMapper extends BaseMapper<OmsOrderRelationProduct> {

}

