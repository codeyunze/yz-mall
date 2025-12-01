package com.yz.mall.sys.service.impl;

import cn.hutool.core.annotation.Link;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.sys.dto.SysDictionaryAddDto;
import com.yz.mall.sys.dto.SysDictionaryQueryDto;
import com.yz.mall.sys.dto.SysDictionaryUpdateDto;
import com.yz.mall.sys.entity.SysDictionary;
import com.yz.mall.sys.mapper.SysDictionaryMapper;
import com.yz.mall.sys.service.SysDictionaryService;
import com.yz.mall.sys.vo.ExtendSysDictionaryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统字典表(SysDictionary)表服务实现类
 *
 * @author yunze
 * @since 2025-11-30 09:53:52
 */
@Slf4j
@Service
public class SysDictionaryServiceImpl extends ServiceImpl<SysDictionaryMapper, SysDictionary> implements SysDictionaryService {

    @Override
    public Long save(SysDictionaryAddDto dto) {
        LambdaQueryWrapper<SysDictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictionary::getParentId, dto.getParentId() != null ? dto.getParentId() : 0L);
        List<SysDictionary> dictionaries = baseMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(dictionaries)) {
            for (SysDictionary dictionary : dictionaries) {
                if (dictionary.getDictionaryKey().equals(dto.getDictionaryKey())) {
                    throw new BusinessException("数据字典 Key 已经存在，不可重复新增");
                }
            }
        } else if (dto.getParentId() != null && dto.getParentId() != 0L) {
            SysDictionary dictionary = baseMapper.selectById(dto.getParentId());
            if (dictionary == null) {
                throw new BusinessException("父级数据字典不存在");
            }
        }

        SysDictionary bo = new SysDictionary();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysDictionaryUpdateDto dto) {
        SysDictionary dictionary = baseMapper.selectById(dto.getId());
        if (dictionary == null) {
            throw new BusinessException("数据字典不存在");
        }
        SysDictionary bo = new SysDictionary();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysDictionary> page(PageFilter<SysDictionaryQueryDto> filter) {
        LambdaQueryWrapper<SysDictionary> queryWrapper = new LambdaQueryWrapper<>();
        if (filter.getFilter() != null) {
            SysDictionaryQueryDto queryDto = filter.getFilter();
            queryWrapper.eq(queryDto.getId() != null, SysDictionary::getId, filter.getFilter().getId());
            queryWrapper.like(StringUtils.hasText(queryDto.getDictionaryKey()), SysDictionary::getDictionaryKey, queryDto.getDictionaryKey());
            queryWrapper.eq(SysDictionary::getParentId, queryDto.getParentId() != null ? filter.getFilter().getParentId() : 0L);
        }
        queryWrapper.orderByAsc(SysDictionary::getSortOrder);
        log.info("数据库请求开始时间: {}", LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_MS_PATTERN));
        Page<SysDictionary> page = baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        log.info("数据库请求结束时间: {}", LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_MS_PATTERN));
        return page;
    }

    @Override
    public ExtendSysDictionaryVo getByKey(String key) {
        List<SysDictionary> dictionaries = baseMapper.selectRecursionByKey(key);
        if (CollectionUtils.isEmpty(dictionaries)) {
            throw new BusinessException("数据字典不存在");
        }
        // 将 dictionaries 以 parentId 为分组字段进行分组，赋值给 dictionaryMap
        Map<Long, List<SysDictionary>>  dictionaryMap = dictionaries.stream().collect(Collectors.groupingBy(SysDictionary::getParentId, LinkedHashMap::new, Collectors.toList()));

        ExtendSysDictionaryVo result = new ExtendSysDictionaryVo();
        // 根目录节点--顶层数据子弹
        SysDictionary rootDic = dictionaryMap.get(0L).get(0);
        BeanUtils.copyProperties(rootDic, result);

        dictionaryMap.remove(0L);

        assembly(result, dictionaryMap);

        return result;
    }

    private void assembly(ExtendSysDictionaryVo result, Map<Long, List<SysDictionary>>  dictionaryMap) {
        List<SysDictionary> dictionaries = dictionaryMap.get(result.getId());
        if (CollectionUtils.isEmpty(dictionaries)) {
            return;
        }
        for (SysDictionary dictionary : dictionaries) {
            ExtendSysDictionaryVo child = new ExtendSysDictionaryVo();
            BeanUtils.copyProperties(dictionary, child);
            // 递归组装子节点的子节点数据
            assembly(child, dictionaryMap);
            result.getChildren().add(child);
        }
    }
}

