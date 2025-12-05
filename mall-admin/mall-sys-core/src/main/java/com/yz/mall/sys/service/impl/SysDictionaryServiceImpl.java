package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.redis.RedissonLockKey;
import com.yz.mall.sys.dto.SysDictionaryAddDto;
import com.yz.mall.sys.dto.SysDictionaryQueryDto;
import com.yz.mall.sys.dto.SysDictionaryUpdateDto;
import com.yz.mall.sys.entity.SysDictionary;
import com.yz.mall.sys.mapper.SysDictionaryMapper;
import com.yz.mall.sys.service.SysDictionaryService;
import com.yz.mall.sys.vo.ExtendSysDictionaryVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

    private final RedisTemplate<String, Object> redisTemplate;

    private final Redisson redisson;

    public SysDictionaryServiceImpl(RedisTemplate<String, Object> redisTemplate
            , Redisson redisson) {
        this.redisTemplate = redisTemplate;
        this.redisson = redisson;
    }

    @Override
    public boolean update(SysDictionaryUpdateDto dto) {
        SysDictionary dictionary = baseMapper.selectById(dto.getId());
        if (dictionary == null) {
            throw new BusinessException("数据字典不存在");
        }
        SysDictionary bo = new SysDictionary();
        BeanUtils.copyProperties(dto, bo);

        boolean updated = baseMapper.updateById(bo) > 0;
        if (updated) {
            // 清理缓存，更新前更新后的都要清理
            // 更新前缓存
            String dictionaryKeyUpdateBefore;
            if (dictionary.getAncestorId() == 0L) {
                // 是第一层顶级数据字典
                dictionaryKeyUpdateBefore = dictionary.getDictionaryKey();
            } else {
                dictionaryKeyUpdateBefore = baseMapper.getKeyById(dictionary.getAncestorId());
            }
            redisTemplate.delete(RedisCacheKey.dictionary(dictionaryKeyUpdateBefore));

            // 更新后缓存
            String dictionaryKeyUpdateAfter;
            if (bo.getAncestorId() == null || bo.getAncestorId() == 0L) {
                // 是第一层顶级数据字典
                dictionaryKeyUpdateAfter = bo.getDictionaryKey();
            } else {
                // 是第二层及以后得数据字典，需要清理其祖宗数据字典缓存
                dictionaryKeyUpdateAfter = baseMapper.getKeyById(bo.getAncestorId());
            }
            redisTemplate.delete(RedisCacheKey.dictionary(dictionaryKeyUpdateAfter));

        }
        return updated;
    }


    @Override
    public Long save(SysDictionaryAddDto dto) {
        SysDictionary bo = new SysDictionary();

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

        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        if (baseMapper.insert(bo) > 0) {
            // 清理缓存
            String dictionaryKey = bo.getDictionaryKey();
            if (bo.getAncestorId() != null && bo.getAncestorId() != 0L) {
                dictionaryKey = baseMapper.getKeyById(bo.getAncestorId());
            }
            redisTemplate.delete(RedisCacheKey.dictionary(dictionaryKey));
        }

        return bo.getId();
    }

    @Override
    public Page<SysDictionary> page(PageFilter<SysDictionaryQueryDto> filter) {
        LambdaQueryWrapper<SysDictionary> queryWrapper = new LambdaQueryWrapper<>();
        if (filter.getFilter() != null) {
            SysDictionaryQueryDto queryDto = filter.getFilter();
            queryWrapper.eq(queryDto.getId() != null, SysDictionary::getId, queryDto.getId());
            queryWrapper.like(StringUtils.hasText(queryDto.getDictionaryKey()), SysDictionary::getDictionaryKey, queryDto.getDictionaryKey());
            queryWrapper.eq(SysDictionary::getParentId, queryDto.getParentId() != null ? queryDto.getParentId() : 0L);
        }
        queryWrapper.orderByAsc(SysDictionary::getSortOrder);
        Page<SysDictionary> page = baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        return page;
    }

    @Override
    public ExtendSysDictionaryVo getByKey(String key) {
        // 第一次从缓存获取数据字典
        ExtendSysDictionaryVo result = selectByKeyFromCache(key);
        if (result != null) {
            return result;
        }

        RLock rLock = redisson.getLock(RedissonLockKey.keyUpdateDic(key));
        try {
            if (rLock.tryLock(20, TimeUnit.SECONDS)) {
                try {
                    // 第二次从缓存获取数据字典
                    result = selectByKeyFromCache(key);
                    if (result != null) {
                        return result;
                    }
                    // 从数据库查询数据
                    result = selectByKeyFromDB(key);
                    // 缓存数据字典
                    cacheDictionary(result);
                } finally {
                    if (rLock.isHeldByCurrentThread()) {
                        rLock.unlock();
                    }
                }
            } else {
                log.warn("获取数据字典锁失败，key: {}", key);
                // 锁获取失败，直接查询数据库
                result = selectByKeyFromDB(key);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取数据字典锁被中断，key: {}", key, e);
            result = selectByKeyFromDB(key);
        }
        return result;
    }

    /**
     * 从缓存获取数据字典
     *
     * @param key 数据字典键
     * @return 数据字典
     */
    private ExtendSysDictionaryVo selectByKeyFromCache(String key) {
        if (!redisTemplate.hasKey(RedisCacheKey.dictionary(key))) {
            // 数据字典不存在缓存
            return null;
        }

        Object obj = redisTemplate.boundValueOps(RedisCacheKey.dictionary(key)).get();
        if (obj == null || !StringUtils.hasText(obj.toString())) {
            // 缓存里数据为空
            return new ExtendSysDictionaryVo();
        }

        try {
            return JacksonUtil.getObjectMapper().readValue(obj.toString(), ExtendSysDictionaryVo.class);
        } catch (JsonProcessingException e) {
            log.error("解析数据字典缓存失败，key: {}", key, e);
            throw new BusinessException("数据字典缓存解析失败");
        }
    }

    /**
     * 缓存数据字典
     *
     * @param result 数据字典
     */
    private void cacheDictionary(ExtendSysDictionaryVo result) {
        try {
            String str = JacksonUtil.getObjectMapper().writeValueAsString(result);
            redisTemplate.boundValueOps(RedisCacheKey.dictionary(result.getDictionaryKey())).set(str, 1, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            log.error("缓存数据字典失败，key: {}", result.getDictionaryKey(), e);
            // 缓存失败不影响主流程，只记录日志
        }
    }

    private ExtendSysDictionaryVo selectByKeyFromDB(String key) {
        List<SysDictionary> dictionaries = baseMapper.selectRecursionByKey(key);
        if (CollectionUtils.isEmpty(dictionaries)) {
            throw new BusinessException("数据字典不存在");
        }
        // 将 dictionaries 以 parentId 为分组字段进行分组，赋值给 dictionaryMap
        Map<Long, List<SysDictionary>> dictionaryMap = dictionaries.stream().collect(Collectors.groupingBy(SysDictionary::getParentId, LinkedHashMap::new, Collectors.toList()));

        ExtendSysDictionaryVo result = new ExtendSysDictionaryVo();
        // 根目录节点--顶层数据字典
        List<SysDictionary> rootDictionaries = dictionaryMap.get(0L);
        if (CollectionUtils.isEmpty(rootDictionaries)) {
            throw new BusinessException("数据字典根节点不存在");
        }
        SysDictionary rootDic = rootDictionaries.get(0);
        BeanUtils.copyProperties(rootDic, result);

        dictionaryMap.remove(0L);

        // 字典数据组装为树形结构
        assembly(result, dictionaryMap);
        return result;
    }

    /**
     * 数据组装
     *
     * @param result        根目录节点
     * @param dictionaryMap 数据字典信息
     */
    private void assembly(ExtendSysDictionaryVo result, Map<Long, List<SysDictionary>> dictionaryMap) {
        List<SysDictionary> dictionaries = dictionaryMap.get(result.getId());
        if (CollectionUtils.isEmpty(dictionaries)) {
            return;
        }
        for (SysDictionary dictionary : dictionaries) {
            ExtendSysDictionaryVo child = new ExtendSysDictionaryVo();
            BeanUtils.copyProperties(dictionary, child);
            // 递归组装子节点的子节点数据
            assembly(child, dictionaryMap);

            if (CollectionUtils.isEmpty(result.getChildren())) {
                result.setChildren(new ArrayList<>());
            }
            result.getChildren().add(child);
        }
    }
}

