package com.yz.dynamic.datasource.one.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.dynamic.datasource.one.dto.StockAddDto;
import com.yz.dynamic.datasource.one.entity.TStock;
import org.apache.ibatis.annotations.Insert;

/**
 * 库存信息(TStock)表数据库访问层
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
public interface TStockMapper extends BaseMapper<TStock> {

    @Insert("INSERT INTO t_stock(id, productId, num) VALUES (#{id}, #{productId}, #{num})")
    Integer save(StockAddDto dto);
}

