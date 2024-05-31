package com.yz.cases.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.dynamic.datasource.one.dto.TStorageAddDto;
import com.yz.dynamic.datasource.one.entity.TStorage;
import org.apache.ibatis.annotations.Insert;

/**
 * 库存信息(TStock)表数据库访问层
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
public interface TStorageMapper extends BaseMapper<TStorage> {

    @Insert("INSERT INTO t_stock(id, product_id, num) VALUES (#{id}, #{productId}, #{num})")
    Integer save(TStorageAddDto dto);
}

