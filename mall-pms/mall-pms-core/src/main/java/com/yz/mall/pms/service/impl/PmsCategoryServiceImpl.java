package com.yz.mall.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.base.exception.DataNotExistException;
import com.yz.mall.pms.dto.PmsCategoryAddDto;
import com.yz.mall.pms.dto.PmsCategoryQueryDto;
import com.yz.mall.pms.dto.PmsCategoryUpdateDto;
import com.yz.mall.pms.entity.PmsCategory;
import com.yz.mall.pms.mapper.PmsCategoryMapper;
import com.yz.mall.pms.service.PmsCategoryService;
import com.yz.mall.pms.vo.PmsCategoryVo;
import com.yz.mall.base.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类表(PmsCategory)表服务实现类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Service
public class PmsCategoryServiceImpl extends ServiceImpl<PmsCategoryMapper, PmsCategory> implements PmsCategoryService {

    @Transactional
    @Override
    public Long save(PmsCategoryAddDto dto) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<PmsCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PmsCategory::getCategoryName, dto.getCategoryName());
        if (baseMapper.exists(queryWrapper)) {
            throw new BusinessException("分类名称已存在");
        }

        // 如果指定了父分类，验证父分类是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            PmsCategory parent = baseMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new DataNotExistException("父分类不存在");
            }
        }

        PmsCategory category = new PmsCategory();
        BeanUtils.copyProperties(dto, category);
        category.setId(IdUtil.getSnowflakeNextId());
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        baseMapper.insert(category);
        return category.getId();
    }

    @Override
    public boolean update(PmsCategoryUpdateDto dto) {
        PmsCategory category = baseMapper.selectById(dto.getId());
        if (category == null) {
            throw new DataNotExistException("分类信息不存在");
        }

        // 如果修改了分类名称，检查新名称是否已存在
        if (StringUtils.hasText(dto.getCategoryName()) && !dto.getCategoryName().equals(category.getCategoryName())) {
            LambdaQueryWrapper<PmsCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PmsCategory::getCategoryName, dto.getCategoryName());
            queryWrapper.ne(PmsCategory::getId, dto.getId());
            long count = baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException("分类名称已存在");
            }
        }

        // 如果修改了父分类，验证父分类是否存在且不能是自己
        if (dto.getParentId() != null) {
            if (dto.getParentId().equals(dto.getId())) {
                throw new BusinessException("不能将自己设置为父分类");
            }
            if (dto.getParentId() > 0) {
                PmsCategory parent = baseMapper.selectById(dto.getParentId());
                if (parent == null) {
                    throw new DataNotExistException("父分类不存在");
                }
                // 检查是否会形成循环引用
                if (isCircularReference(dto.getId(), dto.getParentId())) {
                    throw new BusinessException("不能将子分类设置为父分类，避免循环引用");
                }
            }
        }

        BeanUtils.copyProperties(dto, category);
        return baseMapper.updateById(category) > 0;
    }

    /**
     * 检查是否会形成循环引用
     *
     * @param categoryId 当前分类ID
     * @param newParentId 新的父分类ID
     * @return 是否形成循环引用
     */
    private boolean isCircularReference(Long categoryId, Long newParentId) {
        Long currentParentId = newParentId;
        while (currentParentId != null && currentParentId > 0) {
            if (currentParentId.equals(categoryId)) {
                return true;
            }
            PmsCategory parent = baseMapper.selectById(currentParentId);
            if (parent == null) {
                break;
            }
            currentParentId = parent.getParentId();
        }
        return false;
    }

    @Override
    public Page<PmsCategory> page(PageFilter<PmsCategoryQueryDto> filter) {
        PmsCategoryQueryDto query = filter.getFilter();
        LambdaQueryWrapper<PmsCategory> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(query.getParentId() != null, PmsCategory::getParentId, query.getParentId());
        queryWrapper.like(StringUtils.hasText(query.getCategoryName()), PmsCategory::getCategoryName, query.getCategoryName());

        queryWrapper.orderByDesc(PmsCategory::getSortOrder);
        queryWrapper.orderByDesc(PmsCategory::getId);
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @Override
    public PmsCategoryVo detail(Long id) {
        PmsCategory category = baseMapper.selectById(id);
        if (category == null) {
            throw new DataNotExistException("分类信息不存在");
        }
        PmsCategoryVo vo = new PmsCategoryVo();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }

    @Override
    public List<PmsCategoryVo> tree() {
        // 查询所有分类
        List<PmsCategory> allCategories = baseMapper.selectList(null);
        if (allCategories == null || allCategories.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为VO
        List<PmsCategoryVo> categoryVos = allCategories.stream().map(category -> {
            PmsCategoryVo vo = new PmsCategoryVo();
            BeanUtils.copyProperties(category, vo);
            return vo;
        }).collect(Collectors.toList());

        // 构建树形结构
        return buildTree(categoryVos, 0L);
    }

    @Override
    public List<PmsCategoryVo> listByParentId(Long parentId) {
        LambdaQueryWrapper<PmsCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PmsCategory::getParentId, parentId == null ? 0L : parentId);
        queryWrapper.orderByDesc(PmsCategory::getSortOrder);
        queryWrapper.orderByDesc(PmsCategory::getId);

        List<PmsCategory> categories = baseMapper.selectList(queryWrapper);
        return categories.stream().map(category -> {
            PmsCategoryVo vo = new PmsCategoryVo();
            BeanUtils.copyProperties(category, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 构建树形结构
     *
     * @param allCategories 所有分类
     * @param parentId 父分类ID
     * @return 树形结构列表
     */
    private List<PmsCategoryVo> buildTree(List<PmsCategoryVo> allCategories, Long parentId) {
        List<PmsCategoryVo> result = new ArrayList<>();
        for (PmsCategoryVo category : allCategories) {
            if (category.getParentId() != null && category.getParentId().equals(parentId)) {
                // 递归查找子分类
                List<PmsCategoryVo> children = buildTree(allCategories, category.getId());
                category.setChildren(children);
                result.add(category);
            }
        }
        return result;
    }
}

