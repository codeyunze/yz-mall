package com.yz.mall.pms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.pms.dto.PmsCategoryAddDto;
import com.yz.mall.pms.dto.PmsCategoryQueryDto;
import com.yz.mall.pms.dto.PmsCategoryUpdateDto;
import com.yz.mall.pms.entity.PmsCategory;
import com.yz.mall.pms.vo.PmsCategoryVo;
import com.yz.mall.base.PageFilter;

import java.util.List;

/**
 * 商品分类表(PmsCategory)表服务接口
 *
 * @author yunze
 * @since 2025-01-XX
 */
public interface PmsCategoryService extends IService<PmsCategory> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(PmsCategoryAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(PmsCategoryUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<PmsCategory> page(PageFilter<PmsCategoryQueryDto> filter);

    /**
     * 分类详情
     *
     * @param id 分类id
     * @return 分类详情
     */
    PmsCategoryVo detail(Long id);

    /**
     * 查询所有分类（树形结构）
     *
     * @return 分类树形列表
     */
    List<PmsCategoryVo> tree();

    /**
     * 根据父分类ID查询子分类列表
     *
     * @param parentId 父分类ID，0表示查询顶级分类
     * @return 子分类列表
     */
    List<PmsCategoryVo> listByParentId(Long parentId);
}

