package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.redis.RedissonLockKey;
import com.yz.mall.sys.dto.SysDictionaryAddDto;
import com.yz.mall.sys.dto.SysDictionaryQueryDto;
import com.yz.mall.sys.dto.SysDictionaryUpdateDto;
import com.yz.mall.sys.entity.SysDictionary;
import com.yz.mall.sys.mapper.SysDictionaryMapper;
import com.yz.mall.sys.service.impl.SysDictionaryServiceImpl;
import com.yz.mall.sys.utils.CaffeineCacheUtil;
import com.yz.mall.sys.vo.ExtendSysDictionaryVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 数据字典服务单元测试
 *
 * @author yunze
 * @since 2025-12-05
 */
@ExtendWith(MockitoExtension.class)
class SysDictionaryServiceTest {

    @Mock
    private SysDictionaryMapper dictionaryMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private Redisson redisson;

    @Mock
    private CaffeineCacheUtil caffeineCacheUtil;

    @InjectMocks
    private SysDictionaryServiceImpl dictionaryService;

    @BeforeEach
    void setUp() {
        // 通过反射设置 baseMapper
        ReflectionTestUtils.setField(dictionaryService, "baseMapper", dictionaryMapper);
    }

    @Test
    @DisplayName("新增数据字典-成功")
    void save_Success() {
        // 1. 准备测试数据
        SysDictionaryAddDto addDto = new SysDictionaryAddDto();
        addDto.setDictionaryKey("test_key");
        addDto.setDictionaryValue("test_value");
        addDto.setParentId(0L);
        addDto.setSortOrder(1);
        addDto.setDictionaryEnable(0);

        // 2. 定义 Mock 行为
        when(dictionaryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(dictionaryMapper.insert(any(SysDictionary.class))).thenReturn(1);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // 3. 调用被测试方法
        Long result = dictionaryService.save(addDto);

        // 4. 验证结果
        // 相关的断言方法：
        // assertEquals(expected, actual): 验证两个值相等
        // assertTrue(condition): 验证条件为真
        // assertFalse(condition): 验证条件为假
        // assertNull(object): 验证对象为 null
        // assertThrows(exceptionClass, executable): 验证抛出指定异常
        assertNotNull(result);
        verify(dictionaryMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(dictionaryMapper, times(1)).insert(any(SysDictionary.class));
        verify(redisTemplate, times(1)).delete(anyString());
        verify(caffeineCacheUtil, times(1)).invalidate(anyString());
    }

    @Test
    @DisplayName("新增数据字典-父节点不存在")
    void save_ParentNotExists() {
        // 1. 准备测试数据
        SysDictionaryAddDto addDto = new SysDictionaryAddDto();
        addDto.setDictionaryKey("test_key");
        addDto.setDictionaryValue("test_value");
        addDto.setParentId(999L); // 不存在的父节点ID

        // 2. 定义 Mock 行为
        when(dictionaryMapper.selectById(999L)).thenReturn(null);

        // 3. 调用被测试方法并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            dictionaryService.save(addDto);
        });

        assertEquals("父级数据字典不存在", exception.getMessage());
        verify(dictionaryMapper, times(1)).selectById(999L);
        verify(dictionaryMapper, never()).insert(any(SysDictionary.class));
    }

    @Test
    @DisplayName("新增数据字典-Key已存在")
    void save_KeyExists() {
        // 1. 准备测试数据
        SysDictionaryAddDto addDto = new SysDictionaryAddDto();
        addDto.setDictionaryKey("existing_key");
        addDto.setDictionaryValue("test_value");
        addDto.setParentId(0L);

        SysDictionary existing = new SysDictionary();
        existing.setId(1L);
        existing.setDictionaryKey("existing_key");

        // 2. 定义 Mock 行为
        when(dictionaryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

        // 3. 调用被测试方法并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            dictionaryService.save(addDto);
        });

        assertEquals("数据字典 Key 已经存在，不可重复新增", exception.getMessage());
        verify(dictionaryMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(dictionaryMapper, never()).insert(any(SysDictionary.class));
    }

    @Test
    @DisplayName("新增数据字典-有父节点")
    void save_WithParent() {
        // 1. 准备测试数据
        SysDictionaryAddDto addDto = new SysDictionaryAddDto();
        addDto.setDictionaryKey("child_key");
        addDto.setDictionaryValue("child_value");
        addDto.setParentId(100L);

        SysDictionary parent = new SysDictionary();
        parent.setId(100L);
        parent.setParentId(0L);
        parent.setAncestorId(0L);
        parent.setDictionaryKey("parent_key");

        // 2. 定义 Mock 行为
        when(dictionaryMapper.selectById(100L)).thenReturn(parent);
        when(dictionaryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(dictionaryMapper.insert(any(SysDictionary.class))).thenReturn(1);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // 3. 调用被测试方法
        Long result = dictionaryService.save(addDto);

        // 4. 验证结果
        assertNotNull(result);
        verify(dictionaryMapper, times(1)).selectById(100L);
        verify(dictionaryMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(dictionaryMapper, times(1)).insert(any(SysDictionary.class));
    }

    @Test
    @DisplayName("更新数据字典-成功")
    void update_Success() {
        // 1. 准备测试数据
        SysDictionaryUpdateDto updateDto = new SysDictionaryUpdateDto();
        updateDto.setId(1L);
        updateDto.setDictionaryKey("updated_key");
        updateDto.setDictionaryValue("updated_value");

        SysDictionary existing = new SysDictionary();
        existing.setId(1L);
        existing.setDictionaryKey("old_key");
        existing.setParentId(0L);
        existing.setAncestorId(0L);

        // 2. 定义 Mock 行为
        when(dictionaryMapper.selectById(1L)).thenReturn(existing);
        when(dictionaryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(dictionaryMapper.updateById(any(SysDictionary.class))).thenReturn(1);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // 3. 调用被测试方法
        boolean result = dictionaryService.update(updateDto);

        // 4. 验证结果
        assertTrue(result);
        verify(dictionaryMapper, times(1)).selectById(1L);
        verify(dictionaryMapper, times(1)).updateById(any(SysDictionary.class));
        verify(redisTemplate, atLeastOnce()).delete(anyString());
        verify(caffeineCacheUtil, atLeastOnce()).invalidate(anyString());
    }

    @Test
    @DisplayName("更新数据字典-数据不存在")
    void update_NotExists() {
        // 1. 准备测试数据
        SysDictionaryUpdateDto updateDto = new SysDictionaryUpdateDto();
        updateDto.setId(999L);

        // 2. 定义 Mock 行为
        when(dictionaryMapper.selectById(999L)).thenReturn(null);

        // 3. 调用被测试方法并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            dictionaryService.update(updateDto);
        });

        assertEquals("数据字典不存在", exception.getMessage());
        verify(dictionaryMapper, times(1)).selectById(999L);
        verify(dictionaryMapper, never()).updateById(any(SysDictionary.class));
    }

    @Test
    @DisplayName("更新数据字典-Key已存在")
    void update_KeyExists() {
        // 1. 准备测试数据
        SysDictionaryUpdateDto updateDto = new SysDictionaryUpdateDto();
        updateDto.setId(1L);
        updateDto.setDictionaryKey("existing_key");

        SysDictionary existing = new SysDictionary();
        existing.setId(1L);
        existing.setDictionaryKey("old_key");
        existing.setParentId(0L);

        SysDictionary conflict = new SysDictionary();
        conflict.setId(2L);
        conflict.setDictionaryKey("existing_key");

        // 2. 定义 Mock 行为
        when(dictionaryMapper.selectById(1L)).thenReturn(existing);
        when(dictionaryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(conflict);

        // 3. 调用被测试方法并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            dictionaryService.update(updateDto);
        });

        assertEquals("数据字典 Key 已经存在，不可重复", exception.getMessage());
        verify(dictionaryMapper, times(1)).selectById(1L);
        verify(dictionaryMapper, never()).updateById(any(SysDictionary.class));
    }

    @Test
    @DisplayName("更新数据字典-不能将父节点设置为自己")
    void update_ParentIdEqualsSelf() {
        // 1. 准备测试数据
        SysDictionaryUpdateDto updateDto = new SysDictionaryUpdateDto();
        updateDto.setId(1L);
        updateDto.setParentId(1L); // 父节点ID等于自己

        SysDictionary existing = new SysDictionary();
        existing.setId(1L);
        existing.setParentId(0L);
        existing.setAncestorId(0L);

        // 2. 定义 Mock 行为
        when(dictionaryMapper.selectById(1L)).thenReturn(existing);

        // 3. 调用被测试方法并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            dictionaryService.update(updateDto);
        });

        assertEquals("不能将父节点设置为自己", exception.getMessage());
        verify(dictionaryMapper, times(1)).selectById(1L);
        verify(dictionaryMapper, never()).updateById(any(SysDictionary.class));
    }

    @Test
    @DisplayName("分页查询-成功")
    void page_Success() {
        // 1. 准备测试数据
        PageFilter<SysDictionaryQueryDto> filter = new PageFilter<>();
        filter.setCurrent(1L);
        filter.setSize(10L);
        filter.setFilter(new SysDictionaryQueryDto());

        // 分页查询返回的数据
        Page<SysDictionary> page = new Page<>(1, 10);
        List<SysDictionary> records = new ArrayList<>();
        SysDictionary root = new SysDictionary();
        root.setId(1L);
        root.setParentId(0L);
        root.setAncestorId(0L);
        root.setDictionaryKey("root_key");
        root.setSortOrder(1);
        records.add(root);
        page.setRecords(records);
        page.setTotal(1);

        // 递归查询返回的数据
        List<SysDictionary> recursionList = new ArrayList<>();
        recursionList.add(root);

        // 2. 定义 Mock 行为
        when(dictionaryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(dictionaryMapper.selectRecursionByIds(anyList())).thenReturn(recursionList);

        // 3. 调用被测试方法
        Page<ExtendSysDictionaryVo> result = dictionaryService.page(filter);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("root_key", result.getRecords().get(0).getDictionaryKey());
        verify(dictionaryMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        verify(dictionaryMapper, times(1)).selectRecursionByIds(anyList());
    }

    @Test
    @DisplayName("分页查询-无数据")
    void page_NoData() {
        // 1. 准备测试数据
        PageFilter<SysDictionaryQueryDto> filter = new PageFilter<>();
        filter.setCurrent(1L);
        filter.setSize(10L);
        filter.setFilter(new SysDictionaryQueryDto());

        Page<SysDictionary> page = new Page<>(1, 10);
        page.setTotal(0);
        page.setRecords(Collections.emptyList());

        // 2. 定义 Mock 行为
        when(dictionaryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        // 3. 调用被测试方法
        Page<ExtendSysDictionaryVo> result = dictionaryService.page(filter);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
        verify(dictionaryMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        verify(dictionaryMapper, never()).selectRecursionByIds(anyList());
    }

    @Test
    @DisplayName("根据Key获取数据字典-从缓存获取")
    void getByKey_FromCache() {
        // 1. 准备测试数据
        String key = "test_key";
        String cacheKey = RedisCacheKey.dictionary(key);

        ExtendSysDictionaryVo cachedVo = new ExtendSysDictionaryVo();
        cachedVo.setId(1L);
        cachedVo.setDictionaryKey(key);
        cachedVo.setDictionaryValue("test_value");

        // 2. 定义 Mock 行为
        when(caffeineCacheUtil.getIfPresent(eq(cacheKey), eq(ExtendSysDictionaryVo.class))).thenReturn(cachedVo);

        // 3. 调用被测试方法
        ExtendSysDictionaryVo result = dictionaryService.getByKey(key);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(key, result.getDictionaryKey());
        assertEquals("test_value", result.getDictionaryValue());
        verify(caffeineCacheUtil, times(1)).getIfPresent(eq(cacheKey), eq(ExtendSysDictionaryVo.class));
        verify(dictionaryMapper, never()).selectRecursionByKey(anyString());
    }

    @Test
    @DisplayName("根据Key获取数据字典-从Redis缓存获取")
    void getByKey_FromRedisCache() {
        // 1. 准备测试数据
        String key = "test_key";
        String cacheKey = RedisCacheKey.dictionary(key);

        ExtendSysDictionaryVo vo = new ExtendSysDictionaryVo();
        vo.setId(1L);
        vo.setDictionaryKey(key);
        vo.setDictionaryValue("test_value");

        String jsonValue = "{\"id\":1,\"dictionaryKey\":\"test_key\",\"dictionaryValue\":\"test_value\"}";

        BoundValueOperations<String, Object> boundValueOps = mock(BoundValueOperations.class);

        // 2. 定义 Mock 行为
        when(caffeineCacheUtil.getIfPresent(eq(cacheKey), eq(ExtendSysDictionaryVo.class))).thenReturn(null);
        when(redisTemplate.hasKey(cacheKey)).thenReturn(true);
        when(redisTemplate.boundValueOps(cacheKey)).thenReturn(boundValueOps);
        when(boundValueOps.get()).thenReturn(jsonValue);

        // 3. 调用被测试方法
        ExtendSysDictionaryVo result = dictionaryService.getByKey(key);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(key, result.getDictionaryKey());
        verify(caffeineCacheUtil, times(1)).getIfPresent(eq(cacheKey), eq(ExtendSysDictionaryVo.class));
        verify(redisTemplate, times(1)).hasKey(cacheKey);
        verify(caffeineCacheUtil, times(1)).put(eq(cacheKey), any(ExtendSysDictionaryVo.class));
    }

    @Test
    @DisplayName("根据Key获取数据字典-从数据库获取")
    void getByKey_FromDatabase() throws InterruptedException {
        // 1. 准备测试数据
        String key = "test_key";
        String cacheKey = RedisCacheKey.dictionary(key);

        SysDictionary root = new SysDictionary();
        root.setId(1L);
        root.setParentId(0L);
        root.setAncestorId(0L);
        root.setDictionaryKey(key);
        root.setDictionaryValue("test_value");
        root.setSortOrder(1);

        List<SysDictionary> dictionaries = new ArrayList<>();
        dictionaries.add(root);

        RLock rLock = mock(RLock.class);
        BoundValueOperations<String, Object> boundValueOps = mock(BoundValueOperations.class);

        // 2. 定义 Mock 行为
        when(caffeineCacheUtil.getIfPresent(eq(cacheKey), eq(ExtendSysDictionaryVo.class))).thenReturn(null);
        when(redisTemplate.hasKey(cacheKey)).thenReturn(false);
        when(redisson.getLock(RedissonLockKey.keyUpdateDic(key))).thenReturn(rLock);
        doReturn(true).when(rLock).tryLock(anyLong(), any(TimeUnit.class));
        when(rLock.isHeldByCurrentThread()).thenReturn(true);
        when(dictionaryMapper.selectRecursionByKey(key)).thenReturn(dictionaries);
        when(redisTemplate.boundValueOps(cacheKey)).thenReturn(boundValueOps);

        // 3. 调用被测试方法
        ExtendSysDictionaryVo result = dictionaryService.getByKey(key);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(key, result.getDictionaryKey());
        assertEquals("test_value", result.getDictionaryValue());
        verify(dictionaryMapper, times(1)).selectRecursionByKey(key);
        verify(redisTemplate, times(1)).boundValueOps(cacheKey);
        verify(caffeineCacheUtil, times(1)).put(eq(cacheKey), any(ExtendSysDictionaryVo.class));
        verify(rLock, times(1)).unlock();
    }

    @Test
    @DisplayName("根据Key获取数据字典-数据不存在")
    void getByKey_NotExists() throws InterruptedException {
        // 1. 准备测试数据
        String key = "non_exist_key";
        String cacheKey = RedisCacheKey.dictionary(key);

        RLock rLock = mock(RLock.class);

        // 2. 定义 Mock 行为
        when(caffeineCacheUtil.getIfPresent(eq(cacheKey), eq(ExtendSysDictionaryVo.class))).thenReturn(null);
        when(redisTemplate.hasKey(cacheKey)).thenReturn(false);
        when(redisson.getLock(RedissonLockKey.keyUpdateDic(key))).thenReturn(rLock);
        doReturn(true).when(rLock).tryLock(anyLong(), any(TimeUnit.class));
        when(rLock.isHeldByCurrentThread()).thenReturn(true);
        when(dictionaryMapper.selectRecursionByKey(key)).thenReturn(Collections.emptyList());

        // 3. 调用被测试方法并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            dictionaryService.getByKey(key);
        });

        assertEquals("数据字典不存在", exception.getMessage());
        verify(dictionaryMapper, times(1)).selectRecursionByKey(key);
        verify(rLock, times(1)).unlock();
    }
}

