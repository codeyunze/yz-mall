package com.yz.nacos.mall.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.nacos.mall.stock.entity.TStock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 库存信息(TStock)表数据库访问层
 *
 * @author yunze
 * @since 2023-11-05 15:59:36
 */
public interface TStockMapper extends BaseMapper<TStock> {

    @Update("update t_stock set num = num - #{num} where product_id = #{productId} and num > #{num}")
    public boolean deduct(@Param("productId") Long productId, @Param("num") Integer num);
}

