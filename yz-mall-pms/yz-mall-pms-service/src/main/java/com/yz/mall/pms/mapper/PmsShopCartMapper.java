package com.yz.mall.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsShopCartQueryDto;
import com.yz.mall.pms.entity.PmsShopCart;
import com.yz.mall.pms.vo.PmsShopCartVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 购物车数据表(PmsShopCart)表数据库访问层
 *
 * @author yunze
 * @since 2025-01-24 10:08:17
 */
@Mapper
public interface PmsShopCartMapper extends BaseMapper<PmsShopCart> {

    /**
     * 分页查询购物车信息
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<PmsShopCartVo> selectPageByFilter(Page<Object> page, @Param("filter") PmsShopCartQueryDto filter);
}

