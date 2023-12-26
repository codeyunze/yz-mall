package com.yz.seata.mall.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.seata.mall.storage.entity.TStorage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 库存信息(TStock)表数据库访问层
 *
 * @author yunze
 * @since 2023-11-05 15:59:36
 */
@Mapper
public interface TStorageMapper extends BaseMapper<TStorage> {

    @Update("update t_storage set num = num - #{num} where product_id = #{productId} and num > #{num}")
    public boolean deduct(@Param("productId") Long productId, @Param("num") Integer num);
}

