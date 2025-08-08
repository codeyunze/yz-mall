package com.yz.mall.pms.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.pms.dto.PmsShopCartQueryDto;
import com.yz.mall.pms.entity.PmsShopCart;
import com.yz.mall.pms.vo.PmsShopCartSlimVo;
import com.yz.mall.pms.vo.PmsShopCartVo;
import jakarta.validation.constraints.Size;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    @DS("slave")
    Page<PmsShopCartVo> selectPageByFilter(Page<Object> page, @Param("filter") PmsShopCartQueryDto filter);

    /**
     * 查询购物车商品信息
     *
     * @param userId 用户id
     * @param ids    购物车主键id列表
     * @return 商品信息
     */
    @DS("slave")
    List<PmsShopCartSlimVo> selectCartByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    /**
     * 批量更新购物车数据
     * @param carts 购物车信息
     * @return 操作影响数据行
     */
    @DS("master")
    Integer updateBatchByIds(@Param("carts") @Size(max = 1000) List<PmsShopCart> carts);
}

