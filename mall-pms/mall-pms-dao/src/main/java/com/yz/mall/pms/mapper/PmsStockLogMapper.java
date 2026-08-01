package com.yz.mall.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.pms.entity.PmsStockLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存变更流水 Mapper
 */
@Mapper
public interface PmsStockLogMapper extends BaseMapper<PmsStockLog> {
}
