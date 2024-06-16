package com.yz.mall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.product.dto.MallProductAddDto;
import com.yz.mall.product.dto.MallProductQueryDto;
import com.yz.mall.product.dto.MallProductUpdateDto;
import com.yz.mall.product.entity.MallProduct;
import com.yz.mall.product.vo.MallProductVo;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

/**
 * 商品表(MallProduct)表服务接口
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
public interface MallProductService extends IService<MallProduct> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    String save(MallProductAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid MallProductUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<MallProduct> page(PageFilter<MallProductQueryDto> filter);

    /**
     * 商品详情
     *
     * @param id 商品id
     * @return 商品详情
     */
    MallProductVo detail(String id);

}

