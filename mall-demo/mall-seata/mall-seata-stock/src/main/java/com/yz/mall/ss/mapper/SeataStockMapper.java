package com.yz.mall.ss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.ss.entity.SeataStock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分布式事务-库存表(SeataStock)表数据库访问层
 *
 * @author yunze
 * @since 2025-11-26 15:29:48
 */
@Mapper
public interface SeataStockMapper extends BaseMapper<SeataStock> {

}

