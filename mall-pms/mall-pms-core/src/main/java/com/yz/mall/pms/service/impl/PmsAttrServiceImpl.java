package com.yz.mall.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.dto.PmsAttrAddDto;
import com.yz.mall.pms.dto.PmsAttrQueryDto;
import com.yz.mall.pms.dto.PmsAttrUpdateDto;
import com.yz.mall.pms.entity.PmsAttr;
import com.yz.mall.pms.mapper.PmsAttrMapper;
import com.yz.mall.pms.service.PmsAttrService;
import com.yz.mall.pms.vo.PmsAttrVo;
import com.yz.mall.base.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品规格属性表(PmsAttr)表服务实现类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Service
public class PmsAttrServiceImpl extends ServiceImpl<PmsAttrMapper, PmsAttr> implements PmsAttrService {

    @Transactional
    @Override
    public Long save(PmsAttrAddDto dto) {
        PmsAttr bo = new PmsAttr();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(PmsAttrUpdateDto dto) {
        PmsAttr bo = new PmsAttr();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<PmsAttr> page(PageFilter<PmsAttrQueryDto> filter) {
        PmsAttrQueryDto query = filter.getFilter();
        LambdaQueryWrapper<PmsAttr> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(query.getRelatedId() != null, PmsAttr::getRelatedId, query.getRelatedId());
        queryWrapper.eq(query.getAttrType() != null, PmsAttr::getAttrType, query.getAttrType());
        queryWrapper.like(StringUtils.hasText(query.getAttrName()), PmsAttr::getAttrName, query.getAttrName());
        queryWrapper.like(StringUtils.hasText(query.getAttrValue()), PmsAttr::getAttrValue, query.getAttrValue());

        queryWrapper.orderByDesc(PmsAttr::getId);
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @Override
    public PmsAttrVo detail(Long id) {
        PmsAttr bo = baseMapper.selectById(id);
        PmsAttrVo vo = new PmsAttrVo();
        BeanUtils.copyProperties(bo, vo);
        return vo;
    }

    @Override
    public List<PmsAttrVo> listByRelatedId(Long relatedId) {
        LambdaQueryWrapper<PmsAttr> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PmsAttr::getRelatedId, relatedId);
        queryWrapper.orderByDesc(PmsAttr::getId);
        List<PmsAttr> list = baseMapper.selectList(queryWrapper);
        return list.stream().map(bo -> {
            PmsAttrVo vo = new PmsAttrVo();
            BeanUtils.copyProperties(bo, vo);
            return vo;
        }).collect(Collectors.toList());
    }

}
