package com.yz.mall.seata.at.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.seata.at.order.entity.TOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单信息(TOrder)表数据库访问层
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@Mapper
public interface TOrderMapper extends BaseMapper<TOrder> {

}

