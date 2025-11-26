package com.yz.mall.so.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.so.entity.SeataOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分布式事务-订单表(SeataOrder)表数据库访问层
 *
 * @author yunze
 * @since 2025-11-24 22:40:03
 */
@Mapper
public interface SeataOrderMapper extends BaseMapper<SeataOrder> {

}

