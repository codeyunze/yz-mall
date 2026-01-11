package com.yz.mall.pms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.pms.dto.PmsAttrAddDto;
import com.yz.mall.pms.dto.PmsAttrQueryDto;
import com.yz.mall.pms.dto.PmsAttrUpdateDto;
import com.yz.mall.pms.entity.PmsAttr;
import com.yz.mall.pms.vo.PmsAttrVo;
import com.yz.mall.base.PageFilter;

import jakarta.validation.Valid;

/**
 * 商品属性表(PmsAttr)表服务接口
 *
 * @author yunze
 * @since 2025-01-XX
 */
public interface PmsAttrService extends IService<PmsAttr> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(PmsAttrAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid PmsAttrUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<PmsAttr> page(PageFilter<PmsAttrQueryDto> filter);

    /**
     * 详情查询
     *
     * @param id 属性id
     * @return 属性详情
     */
    PmsAttrVo detail(Long id);

    /**
     * 根据关联ID查询属性列表
     *
     * @param relatedId 关联ID（product_id或sku_id）
     * @return 属性列表
     */
    java.util.List<PmsAttrVo> listByRelatedId(Long relatedId);

}
