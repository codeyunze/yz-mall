package com.yz.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import com.yz.mall.oms.vo.OmsOrderProductVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单商品关联表(OmsOrderRelationProduct)表数据库访问层
 *
 * @author yunze
 * @since 2025-01-30 19:14:03
 */
@Mapper
public interface OmsOrderRelationProductMapper extends BaseMapper<OmsOrderRelationProduct> {

    /**
     * 根据订单Id查询订单商品信息
     *
     * @param orderIds 订单Id列表
     * @return 订单商品信息
     */
    List<OmsOrderProductVo> selectOrderProductByOrderIds(@Param("orderIds") List<Long> orderIds);
}

