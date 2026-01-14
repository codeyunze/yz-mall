package com.yz.mall.pms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.pms.dto.PmsSkuAddDto;
import com.yz.mall.pms.dto.PmsSkuQueryDto;
import com.yz.mall.pms.dto.PmsSkuUpdateDto;
import com.yz.mall.pms.entity.PmsSku;
import com.yz.mall.pms.vo.PmsSkuVo;
import com.yz.mall.base.PageFilter;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 商品SKU表(PmsSku)表服务接口
 *
 * @author yunze
 * @since 2025-01-XX
 */
public interface PmsSkuService extends IService<PmsSku> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(PmsSkuAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid PmsSkuUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<PmsSkuVo> page(PageFilter<PmsSkuQueryDto> filter);

    /**
     * 详情查询
     *
     * @param id SKU id
     * @return SKU详情
     */
    PmsSkuVo detail(Long id);

    /**
     * 根据商品ID查询SKU列表
     *
     * @param productId 商品ID
     * @return SKU列表
     */
    List<PmsSkuVo> listByProductId(Long productId);

}
