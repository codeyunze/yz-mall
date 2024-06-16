package com.yz.mall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.product.dto.MallStockAddDto;
import com.yz.mall.product.dto.MallStockQueryDto;
import com.yz.mall.product.dto.MallStockUpdateDto;
import com.yz.mall.product.entity.MallStock;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

/**
 * 商品库存表(MallStock)表服务接口
 *
 * @author yunze
 * @since 2024-06-16 16:14:09
 */
public interface MallStockService extends IService<MallStock> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    String save(MallStockAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid MallStockUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<MallStock> page(PageFilter<MallStockQueryDto> filter);

    /**
     * 扣减库存
     *
     * @param productId 商品信息
     * @param quantity  扣减数量
     * @return 是否扣减成功
     */
    Boolean deduct(String productId, Integer quantity);

}

