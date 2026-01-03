package com.yz.mall.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.pms.entity.PmsCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类表(PmsCategory)表数据库访问层
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Mapper
public interface PmsCategoryMapper extends BaseMapper<PmsCategory> {
}

