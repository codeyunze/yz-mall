package com.yz.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.oms.entity.OmsOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单信息表(OmsOrder)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-18 12:49:55
 */
@Mapper
public interface OmsOrderMapper extends BaseMapper<OmsOrder> {

}

