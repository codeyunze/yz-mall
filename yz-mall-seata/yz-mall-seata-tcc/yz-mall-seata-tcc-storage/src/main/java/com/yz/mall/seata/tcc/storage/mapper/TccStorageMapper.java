package com.yz.mall.seata.tcc.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.seata.tcc.storage.entity.TccStorage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 库存信息(TccStorage)表数据库访问层
 *
 * @author yunze
 * @since 2023-11-05 15:59:36
 */
@Mapper
public interface TccStorageMapper extends BaseMapper<TccStorage> {

    /**
     * 冻结库存
     *
     * @param productId 商品id
     * @param freezeNum 需要冻结数量
     * @return 影响数据量
     * @apiNote Try: 商品库存-扣减数量，冻结库存+扣减数量
     */
    @Update("update tcc_storage set num = num - #{freezeNum}, freeze_count = freeze_count + #{freezeNum} where product_id = #{productId} and num > #{freezeNum}")
    public Integer freezeStorage(@Param("productId") Long productId, @Param("freezeNum") Integer freezeNum);

    /**
     * 扣减冻结的库存
     *
     * @param productId 商品id
     * @param deductNum 扣减数量
     * @return 影响数据量
     * @apiNote Confirm: 冻结库存-扣减数量 （真正的扣减库存）
     */
    @Update("update tcc_storage set freeze_count = freeze_count - #{deductNum} where product_id = #{productId}")
    public Integer deductFreezeStorage(@Param("productId") Long productId, @Param("deductNum") Integer deductNum);

    /**
     * 解冻库存
     *
     * @param productId   商品id
     * @param unfreezeNum 解冻库存
     * @return 影响数据量
     * @apiNote Cancel: 冻结库存-扣减数量，库存+扣减数量
     */
    @Update("update tcc_storage set freeze_count = freeze_count - #{unfreezeNum}, num = num + #{unfreezeNum} where product_id = #{productId}")
    Integer unfreezeStorage(@Param("productId") Long productId, @Param("unfreezeNum") Integer unfreezeNum);
}

