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
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
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

    private final Cache<String, Object> caffeineCache;

    public SysDictionaryServiceImpl(RedisTemplate<String, Object> redisTemplate
            , Redisson redisson
            , Cache<String, Object> caffeineCache) {
        this.redisTemplate = redisTemplate;
        this.redisson = redisson;
        this.caffeineCache = caffeineCache;
    }

    @Override
    public boolean update(SysDictionaryUpdateDto dto) {
        SysDictionary dictionary = baseMapper.selectById(dto.getId());
        if (dictionary == null) {
            throw new BusinessException("数据字典不存在");
        }

        // 如果修改了 dictionaryKey，检查新 key 是否已存在，使用 limit 1 优化性能
        if (dto.getDictionaryKey() != null && !dto.getDictionaryKey().equals(dictionary.getDictionaryKey())) {
            Long parentId = dto.getParentId() != null ? dto.getParentId() : dictionary.getParentId();
            LambdaQueryWrapper<SysDictionary> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysDictionary::getParentId, parentId);
            queryWrapper.eq(SysDictionary::getDictionaryKey, dto.getDictionaryKey());
            queryWrapper.ne(SysDictionary::getId, dto.getId());
            queryWrapper.last("limit 1");
            SysDictionary existDictionary = baseMapper.selectOne(queryWrapper);
            if (existDictionary != null) {
                throw new BusinessException("数据字典 Key 已经存在，不可重复");
            }
        }

        // 如果修改了 parentId，需要验证和重新计算 ancestorId
        Long newParentId = dto.getParentId() != null ? dto.getParentId() : dictionary.getParentId();
        Long newAncestorId = dictionary.getAncestorId();

        if (!newParentId.equals(dictionary.getParentId())) {
            newAncestorId = loopCheck(dto.getId(), newParentId);
        }

        SysDictionary bo = new SysDictionary();
        BeanUtils.copyProperties(dto, bo);
        bo.setAncestorId(newAncestorId);
        if (bo.getParentId() == null) {
            bo.setParentId(dictionary.getParentId());
        }

        boolean updated = baseMapper.updateById(bo) > 0;
        if (!updated) {
            // 更新失败，直接返回
            return false;
        }

        // 清理缓存，更新前更新后的都要清理
        // 更新前缓存
        String dictionaryKeyUpdateBefore;
        if (dictionary.getAncestorId() == null || dictionary.getAncestorId() == 0L) {
            dictionaryKeyUpdateBefore = dictionary.getDictionaryKey();
        } else {
            String ancestorKey = baseMapper.getKeyById(dictionary.getAncestorId());
            dictionaryKeyUpdateBefore = ancestorKey != null ? ancestorKey : dictionary.getDictionaryKey();
        }
        String cacheKeyBefore = RedisCacheKey.dictionary(dictionaryKeyUpdateBefore);
        redisTemplate.delete(cacheKeyBefore);
        caffeineCache.invalidate(cacheKeyBefore);

        // 更新后缓存
        String dictionaryKeyUpdateAfter;
        if (newAncestorId == null || newAncestorId == 0L) {
            dictionaryKeyUpdateAfter = bo.getDictionaryKey() != null ? bo.getDictionaryKey() : dictionary.getDictionaryKey();
        } else {
            String ancestorKey = baseMapper.getKeyById(newAncestorId);
            dictionaryKeyUpdateAfter = ancestorKey != null ? ancestorKey : (bo.getDictionaryKey() != null ? bo.getDictionaryKey() : dictionary.getDictionaryKey());
        }
        String cacheKeyAfter = RedisCacheKey.dictionary(dictionaryKeyUpdateAfter);
        redisTemplate.delete(cacheKeyAfter);
        caffeineCache.invalidate(cacheKeyAfter);
        return true;
    }

    /**
     * 循环检查，验证循环引用：不能将 parentId 设置为自己的 id 或子节点的 id
     *
     * @param updateDictionaryId 更新字典的主键 Id
     * @param newParentId        新的父节点 ID
     * @return 新的祖先节点 ID
     */
    private Long loopCheck(Long updateDictionaryId, Long newParentId) {
        Long newAncestorId;
        // 验证循环引用：不能将 parentId 设置为自己的 id 或子节点的 id
        if (newParentId.equals(updateDictionaryId)) {
            throw new BusinessException("不能将父节点设置为自己");
        }

        // 检查是否将 parentId 设置为子节点的 id（会造成循环引用）
        if (newParentId == 0L) {
            return 0L;
        }

        // 检查当前数据字典的 parentId ，是否是当前数据字典的后代节点
        if (isDescendant(updateDictionaryId, newParentId)) {
            throw new BusinessException("不能将父节点设置为子节点，会造成循环引用");
        }

        // 验证新的父节点是否存在
        SysDictionary newParent = baseMapper.selectById(newParentId);
        if (newParent == null) {
            throw new BusinessException("父级数据字典不存在");
        }

        // 重新计算 ancestorId
        newAncestorId = (newParent.getAncestorId() == null || newParent.getAncestorId() == 0L)
                ? newParent.getId() : newParent.getAncestorId();
        return newAncestorId;
    }

    /**
     * 检查 targetId 是否是 ancestorId 的后代节点（包括直接子节点和间接子节点）
     *
     * @param ancestorId 祖先节点 ID
     * @param targetId   目标节点 ID
     * @return 如果是后代节点返回 true，否则返回 false
     */
    private boolean isDescendant(Long ancestorId, Long targetId) {
        if (targetId == null || targetId == 0L || ancestorId == null) {
            return false;
        }
        SysDictionary target = baseMapper.selectById(targetId);
        if (target == null) {
            return false;
        }
        // 如果目标节点的 ancestorId 等于 ancestorId，说明是后代
        if (ancestorId.equals(target.getAncestorId())) {
            return true;
        }
        // 如果目标节点的 parentId 等于 ancestorId，说明是直接子节点
        if (ancestorId.equals(target.getParentId())) {
            return true;
        }
        // 递归向上查找父节点链，检查是否包含 ancestorId
        if (target.getParentId() != null && target.getParentId() != 0L) {
            return isDescendant(ancestorId, target.getParentId());
        }
        return false;
    }


    @Override
    public Long save(SysDictionaryAddDto dto) {
        // 验证父节点是否存在并计算 ancestorId
        Long parentId = dto.getParentId() != null ? dto.getParentId() : 0L;
        Long ancestorId = 0L;
        SysDictionary parentDictionary = null;

        if (parentId != 0L) {
            parentDictionary = baseMapper.selectById(parentId);
            if (parentDictionary == null) {
                throw new BusinessException("父级数据字典不存在");
            }
            // 如果父节点是顶级节点（ancestorId 为 0），则当前节点的 ancestorId 为父节点的 id
            // 否则，当前节点的 ancestorId 继承父节点的 ancestorId
            ancestorId = (parentDictionary.getAncestorId() == null || parentDictionary.getAncestorId() == 0L)
                    ? parentDictionary.getId() : parentDictionary.getAncestorId();
        }

        // 检查同一父节点下 Key 是否重复，使用 limit 1 优化性能
        LambdaQueryWrapper<SysDictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictionary::getParentId, parentId);
        queryWrapper.eq(SysDictionary::getDictionaryKey, dto.getDictionaryKey());
        queryWrapper.last("limit 1");
        SysDictionary existDictionary = baseMapper.selectOne(queryWrapper);
        if (existDictionary != null) {
            throw new BusinessException("数据字典 Key 已经存在，不可重复新增");
        }

        SysDictionary bo = new SysDictionary();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setParentId(parentId);
        bo.setAncestorId(ancestorId);

        if (baseMapper.insert(bo) == 0) {
            throw new BusinessException("数据字典保存失败");
        }

        // 清理缓存
        String dictionaryKey = bo.getDictionaryKey();
        if (ancestorId != 0L) {
            String ancestorKey = baseMapper.getKeyById(ancestorId);
            if (ancestorKey != null) {
                dictionaryKey = ancestorKey;
            }
        }
        String cacheKey = RedisCacheKey.dictionary(dictionaryKey);
        redisTemplate.delete(cacheKey);
        caffeineCache.invalidate(cacheKey);
        return bo.getId();
    }

    @Override
    public Page<ExtendSysDictionaryVo> page(PageFilter<SysDictionaryQueryDto> filter) {
        LambdaQueryWrapper<SysDictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysDictionary::getId);
        if (filter.getFilter() != null) {
            SysDictionaryQueryDto queryDto = filter.getFilter();
            queryWrapper.eq(queryDto.getId() != null, SysDictionary::getId, queryDto.getId());
            queryWrapper.like(StringUtils.hasText(queryDto.getDictionaryKey()), SysDictionary::getDictionaryKey, queryDto.getDictionaryKey());
            queryWrapper.eq(SysDictionary::getParentId, queryDto.getParentId() != null ? queryDto.getParentId() : 0L);
            queryWrapper.eq(queryDto.getDictionaryEnable() != null, SysDictionary::getDictionaryEnable, queryDto.getDictionaryEnable());
            queryWrapper.ge(queryDto.getCreateTimeFrom() != null, SysDictionary::getCreateTime, queryDto.getCreateTimeFrom());
            queryWrapper.le(queryDto.getCreateTimeTo() != null, SysDictionary::getCreateTime, queryDto.getCreateTimeTo());
        }
        queryWrapper.orderByAsc(SysDictionary::getSortOrder);
        Page<SysDictionary> page = baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        if (page.getTotal() == 0) {
            return new Page<>();
        }

        List<Long> ids = page.getRecords().stream().map(SysDictionary::getId).collect(Collectors.toList());
        List<SysDictionary> dictionaries = baseMapper.selectRecursionByIds(ids);

        // 根节点顶层数据字典
        List<ExtendSysDictionaryVo> tops = dictionaries.stream().filter(t -> t.getParentId() == 0L).map(t -> {
            ExtendSysDictionaryVo vo = new ExtendSysDictionaryVo();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).sorted(Comparator.comparing(ExtendSysDictionaryVo::getSortOrder)).toList();

        // 将 dictionaries 以 parentId 为分组字段进行分组，赋值给 dictionaryMap
        Map<Long, List<SysDictionary>> dictionaryMap = dictionaries.stream().filter(t -> t.getParentId() != 0L).collect(Collectors.groupingBy(SysDictionary::getParentId));

        tops.forEach(rootDictionaries -> {
            assembly(rootDictionaries, dictionaryMap);
        });

        Page<ExtendSysDictionaryVo> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(tops);
        return result;
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
        String cacheKey = RedisCacheKey.dictionary(key);
        
        // 先从Caffeine本地缓存获取
        Object caffeineValue = caffeineCache.getIfPresent(cacheKey);
        if (caffeineValue != null) {
            if (caffeineValue instanceof ExtendSysDictionaryVo) {
                return (ExtendSysDictionaryVo) caffeineValue;
            }
        }

        // 如果Caffeine缓存不存在，从Redis获取
        if (!redisTemplate.hasKey(cacheKey)) {
            // 数据字典不存在缓存
            return null;
        }

        Object obj = redisTemplate.boundValueOps(cacheKey).get();
        if (obj == null || !StringUtils.hasText(obj.toString())) {
            // 缓存里数据为空
            return new ExtendSysDictionaryVo();
        }

        try {
            ExtendSysDictionaryVo result = JacksonUtil.getObjectMapper().readValue(obj.toString(), ExtendSysDictionaryVo.class);
            // 将Redis中的数据同步到Caffeine缓存
            if (result != null) {
                caffeineCache.put(cacheKey, result);
            }
            return result;
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
            String cacheKey = RedisCacheKey.dictionary(result.getDictionaryKey());
            // 缓存到Redis
            redisTemplate.boundValueOps(cacheKey).set(str, 1, TimeUnit.DAYS);
            // 缓存到Caffeine本地缓存
            caffeineCache.put(cacheKey, result);
        } catch (JsonProcessingException e) {
            log.error("缓存数据字典失败，key: {}", result.getDictionaryKey(), e);
            // 缓存失败不影响主流程，只记录日志
        }
    }

    /**
     * 从数据库查询指定 key 的数据字典信息
     *
     * @param key 指定的数据字典 key
     * @return 数据字典信息
     */
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

