package com.yz.mall.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.pms.entity.PmsStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 商品库存表(PmsStock)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
@Mapper
public interface PmsStockMapper extends BaseMapper<PmsStock> {

    /**
     * 扣减库存
     *
     * @param productId 商品信息
     * @param quantity  扣减数量
     * @return 是否扣减成功
     */
    @Update("update pms_stock set quantity = quantity - #{quantity} where invalid = 0 and product_id = #{productId} and quantity >= #{quantity}")
    boolean deduct(@Param("productId") String productId, @Param("quantity") Integer quantity);

    /**
     * 获取指定商品的库存
     *
     * @param productId 商品id
     * @return 商品剩余库存
     */
    @Select("select quantity from pms_stock where invalid = 0 and product_id = #{productId}")
    Integer getStockByProductId(@Param("productId") String productId);
}

