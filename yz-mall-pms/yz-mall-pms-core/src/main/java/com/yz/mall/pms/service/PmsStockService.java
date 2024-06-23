package com.yz.mall.pms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.pms.dto.PmsStockQueryDto;
import com.yz.mall.pms.entity.PmsStock;
import com.yz.tools.PageFilter;

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
    Page<PmsStock> page(PageFilter<PmsStockQueryDto> filter);

    /**
     * 扣减库存
     *
     * @param productId 商品信息
     * @param quantity  扣减数量
     * @return 是否扣减成功
     */
    Boolean deduct(String productId, Integer quantity);

    /**
     * 增加库存
     *
     * @param productId 商品信息
     * @param quantity  增加数量
     * @return 是否增加成功
     */
    Boolean add(String productId, Integer quantity);

    /**
     * 获取指定商品的库存
     *
     * @param productId 商品id
     * @return 商品剩余库存
     */
    Integer getStockByProductId(String productId);

}

