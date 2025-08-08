package com.yz.mall.pms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.dto.PmsStockQueryDto;
import com.yz.mall.pms.entity.PmsStock;
import com.yz.mall.pms.vo.PmsProductStockVo;
import com.yz.mall.base.PageFilter;

import java.util.List;
import java.util.Map;

/**
 * 商品库存表(PmsStock)表服务接口
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
public interface PmsStockService extends IService<PmsStock> {

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<PmsProductStockVo> page(PageFilter<PmsStockQueryDto> filter);

    /**
     * 扣减库存
     *
     * @param deductStock 指定的商品及其库存
     * @return 是否扣减成功
     */
    Boolean deduct(InternalPmsStockDto deductStock);

    /**
     * 批量扣减指定商品库存
     *
     * @param deductStocks 指定的商品及其库存
     */
    void deduct(List<InternalPmsStockDto> deductStocks);

    /**
     * 增加库存
     *
     * @param addStock 增加商品库存
     * @return 是否增加成功
     */
    Boolean add(InternalPmsStockDto addStock);

    /**
     * 获取指定商品的库存
     *
     * @param productId 商品id
     * @return 商品剩余库存
     */
    Integer getStockByProductId(Long productId);

    /**
     * 获取指定商品的库存
     *
     * @param productIds 商品id
     * @return 商品剩余库存
     */
    Map<Long, Integer> getStockByProductIds(List<Long> productIds);

}

