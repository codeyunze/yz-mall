package com.yz.mall.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.pms.entity.PmsSku;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品SKU表(PmsSku)表数据库访问层
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Mapper
public interface PmsSkuMapper extends BaseMapper<PmsSku> {

}
