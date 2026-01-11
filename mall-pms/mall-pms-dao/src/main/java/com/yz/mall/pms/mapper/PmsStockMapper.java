package com.yz.mall.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsStockQueryDto;
import com.yz.mall.pms.entity.PmsStock;
import com.yz.mall.pms.vo.PmsProductStockVo;
import com.yz.mall.pms.vo.PmsStockVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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
     * @param skuId SKU信息
     * @param quantity  扣减数量
     * @return 是否扣减成功
     */
    @Update("update pms_stock set quantity = quantity - #{quantity} where invalid = 0 and sku_id = #{skuId} and quantity >= #{quantity}")
    boolean deduct(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 获取指定SKU的库存
     *
     * @param skuId SKU id
     * @return SKU剩余库存
     */
    @Select("select quantity from pms_stock where invalid = 0 and sku_id = #{skuId}")
    Integer getStockBySkuId(@Param("skuId") Long skuId);

    /**
     * 分页查询
     *
     * @param page   分页信息
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<PmsProductStockVo> selectPageByFilter(Page<Object> page, @Param("filter") PmsStockQueryDto filter);

    /**
     * 查询指定SKU的库存数量
     *
     * @param skuIds 需要查询的SKU Id
     * @return SKU库存列表数据
     */
    List<PmsStockVo> selectStockBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 锁定库存
     *
     * @param skuId SKU id
     * @param quantity  锁定数量
     * @return 是否锁定成功
     */
    @Update("update pms_stock set quantity = quantity - #{quantity}, locked_quantity = locked_quantity + #{quantity} where invalid = 0 and sku_id = #{skuId} and quantity >= #{quantity}")
    boolean lockStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 释放锁定的库存
     *
     * @param skuId SKU id
     * @param quantity  释放数量
     * @return 是否释放成功
     */
    @Update("update pms_stock set quantity = quantity + #{quantity}, locked_quantity = locked_quantity - #{quantity} where invalid = 0 and sku_id = #{skuId} and locked_quantity >= #{quantity}")
    boolean unlockStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 扣减锁定的库存
     *
     * @param skuId SKU id
     * @param quantity  扣减数量
     * @return 是否扣减成功
     */
    @Update("update pms_stock set locked_quantity = locked_quantity - #{quantity} where invalid = 0 and sku_id = #{skuId} and locked_quantity >= #{quantity}")
    boolean deductLockedStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

}

