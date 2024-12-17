package com.yz.cases.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.cases.mall.dto.TProductAddDto;
import com.yz.cases.mall.dto.TProductQueryDto;
import com.yz.cases.mall.dto.TProductUpdateDto;
import com.yz.cases.mall.entity.TProduct;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

/**
 * 商品信息(TProduct)表服务接口
 *
 * @author yunze
 * @since 2024-06-13 08:38:51
 */
public interface TProductService extends IService<TProduct> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(TProductAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid TProductUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<TProduct> page(PageFilter<TProductQueryDto> filter);

    /**
     * 创建数据表
     */
    void createTable();

    /**
     * 生成商品数据
     */
    void generateData();

}

