package com.yz.cases.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.cases.mall.entity.TProduct;
import org.apache.ibatis.annotations.Param;

/**
 * 商品信息(TProduct)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-13 08:38:51
 */
public interface TProductMapper extends BaseMapper<TProduct> {

    /**
     * 将实时表修改为历史表，并创建新的实时表
     *
     * @param tableName t_product_yyyyMMdd
     */
    void createTable(@Param("tableName") String tableName);

}

