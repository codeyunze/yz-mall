package com.yz.dynamic.datasource.one.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.dynamic.datasource.one.dto.ProductAddDto;
import com.yz.dynamic.datasource.one.entity.TProduct;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * 商品信息(TProduct)表数据库访问层
 *
 * @author yunze
 * @since 2023-10-29 18:01:13
 */
public interface TProductMapper extends BaseMapper<TProduct> {

    @Insert("INSERT INTO t_product(id, name, price) VALUES (#{id}, #{name}, #{price})")
    Integer save(ProductAddDto dto);

    /**
     * 将实时表修改为历史表，并创建新的实时表
     *
     * @param tableName t_product_yyyyMMdd
     */
    void createTable(@Param("tableName") String tableName);

}

