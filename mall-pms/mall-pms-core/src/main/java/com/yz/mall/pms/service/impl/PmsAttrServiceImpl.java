package com.yz.mall.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.dto.PmsAttrAddDto;
import com.yz.mall.pms.dto.PmsAttrQueryDto;
import com.yz.mall.pms.dto.PmsAttrUpdateDto;
import com.yz.mall.pms.entity.PmsAttr;
import com.yz.mall.pms.entity.PmsProduct;
import com.yz.mall.pms.mapper.PmsAttrMapper;
import com.yz.mall.pms.service.PmsAttrService;
import com.yz.mall.pms.service.PmsProductService;
import com.yz.mall.pms.vo.PmsAttrVo;
import com.yz.mall.base.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品规格属性表(PmsAttr)表服务实现类
 *
 * @author yunze
 * @since 2025-01-XX
 */
@Service
public class PmsAttrServiceImpl extends ServiceImpl<PmsAttrMapper, PmsAttr> implements PmsAttrService {

    private final PmsProductService pmsProductService;

    public PmsAttrServiceImpl(PmsProductService pmsProductService) {
        this.pmsProductService = pmsProductService;
    }

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
    public Page<PmsAttrVo> page(PageFilter<PmsAttrQueryDto> filter) {
        PmsAttrQueryDto query = filter.getFilter();
        LambdaQueryWrapper<PmsAttr> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(query.getRelatedId() != null, PmsAttr::getRelatedId, query.getRelatedId());
        queryWrapper.eq(query.getAttrType() != null, PmsAttr::getAttrType, query.getAttrType());
        queryWrapper.like(StringUtils.hasText(query.getAttrName()), PmsAttr::getAttrName, query.getAttrName());
        queryWrapper.like(StringUtils.hasText(query.getAttrValue()), PmsAttr::getAttrValue, query.getAttrValue());

        queryWrapper.orderByDesc(PmsAttr::getId);
        Page<PmsAttr> page = baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        // 从商品表中查询商品名称
        List<Long> productIds = page.getRecords().stream().map(PmsAttr::getRelatedId).distinct().collect(Collectors.toList());
        List<PmsProduct> products = pmsProductService.listByIds(productIds);
        Map<Long, String> productNameMap = products.stream().collect(Collectors.toMap(PmsProduct::getId, PmsProduct::getProductName));
        // 转换Page<PmsAttr>为Page<PmsAttrVo>
        Page<PmsAttrVo> voPage = new Page<>();
        voPage.setTotal(page.getTotal());
        List<PmsAttrVo> voList = page.getRecords().stream().map(item -> {
            PmsAttrVo vo = new PmsAttrVo();
            BeanUtils.copyProperties(item, vo);
            vo.setProductName(productNameMap.get(item.getRelatedId()));
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
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
