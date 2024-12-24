package com.yz.mall.pms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.pms.dto.PmsProductAddDto;
import com.yz.mall.pms.dto.PmsProductQueryDto;
import com.yz.mall.pms.dto.PmsProductUpdateDto;
import com.yz.mall.pms.entity.PmsProduct;
import com.yz.mall.pms.vo.PmsProductVo;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

/**
 * 商品表(PmsProduct)表服务接口
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
public interface PmsProductService extends IService<PmsProduct> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(PmsProductAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid PmsProductUpdateDto dto);

    /**
     * 商品上架
     * @param id 商品Id {@link PmsProduct#getId()}
     * @return 是否上架成功
     */
    boolean publish(Long id);

    /**
     * 商品下架
     * @param id 商品Id {@link PmsProduct#getId()}
     * @return 是否下架成功
     */
    boolean delisting(Long id);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<PmsProduct> page(PageFilter<PmsProductQueryDto> filter);

    /**
     * 商品详情
     *
     * @param id 商品id
     * @return 商品详情
     */
    PmsProductVo detail(Long id);

}

