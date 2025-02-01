package com.yz.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.oms.entity.OmsOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单信息表(OmsOrder)表数据库访问层
 *
 * @author yunze
 * @since 2025-01-30 19:12:59
 */
@Mapper
public interface OmsOrderMapper extends BaseMapper<OmsOrder> {

}

