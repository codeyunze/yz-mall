package com.yz.unqid.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.web.exception.BusinessException;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.RedisCacheKey;
import com.yz.mall.web.common.RedissonLockKey;
import com.yz.unqid.dto.SysUnqidAddDto;
import com.yz.unqid.dto.SysUnqidQueryDto;
import com.yz.unqid.dto.SysUnqidUpdateDto;
import com.yz.unqid.entity.SysUnqid;
import com.yz.unqid.mapper.SysUnqidMapper;
import com.yz.unqid.service.SysUnqidService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 系统-流水号表(SysUnqid)表服务实现类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@Service("sysUnqidServiceImpl")
public class SysUnqidServiceImpl extends ServiceImpl<SysUnqidMapper, SysUnqid> implements SysUnqidService {

    @Resource
    protected Redisson redisson;

    @Resource
    protected RedisTemplate<String, Object> defaultRedisTemplate;

    @Override
    public String save(SysUnqidAddDto dto) {
        SysUnqid bo = new SysUnqid();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextIdStr());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean cachePersistence(SysUnqidUpdateDto dto) {
        SysUnqid bo = new SysUnqid();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public void cachePersistence(String prefix) {
        RLock redissonLock = redisson.getLock(RedissonLockKey.keyUnqid(prefix));
        redissonLock.lock(10, TimeUnit.SECONDS);

        try {
            SysUnqid bo = baseMapper.selectOne(new LambdaQueryWrapper<SysUnqid>().eq(SysUnqid::getPrefix, prefix));

            BoundHashOperations<String, Object, Object> boundHashOps = defaultRedisTemplate.boundHashOps(RedisCacheKey.objUnqid(prefix));
            Integer serialNumber = (Integer) boundHashOps.get("serialNumber");

            if (bo == null) {
                bo = new SysUnqid();
                String id = (String) boundHashOps.get("id");
                bo.setId(id);
                bo.setSerialNumber(serialNumber);
                bo.setPrefix(prefix);
                baseMapper.insert(bo);
            } else {
                bo.setSerialNumber(serialNumber);
                baseMapper.updateById(bo);
            }
            defaultRedisTemplate.delete(RedisCacheKey.objUnqid(prefix));
        } finally {
            redissonLock.unlock();
        }
    }

    @Override
    public Page<SysUnqid> page(PageFilter<SysUnqidQueryDto> filter) {
        LambdaQueryWrapper<SysUnqid> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @Override
    public String generateSerialNumber(String prefix, Integer numberLength) {
        // 加锁用于控制防止出现重复序列号情况
        RLock redissonLock = redisson.getLock(RedissonLockKey.keyUnqid(prefix));
        redissonLock.lock(10, TimeUnit.SECONDS);

        try {
            SysUnqid bo = baseMapper.selectOne(new LambdaQueryWrapper<SysUnqid>().eq(SysUnqid::getPrefix, prefix));
            boolean saved;
            if (bo == null) {
                bo = new SysUnqid();
                bo.setId(IdUtil.getSnowflakeNextIdStr());
                bo.setSerialNumber(1);
                bo.setPrefix(prefix);
                saved = baseMapper.insert(bo) > 0;
            } else {
                bo.setSerialNumber(bo.getSerialNumber() + 1);
                saved = baseMapper.updateById(bo) > 0;
            }

            if (!saved) {
                throw new BusinessException(prefix + "流水号生成失败");
            }

            String code = generateProcessor(prefix, numberLength, bo.getSerialNumber());
            // baseMapper.record(code);

            return code;
        } finally {
            redissonLock.unlock();
        }
    }

    @Override
    public List<String> generateSerialNumbers(String prefix, Integer numberLength, Integer quantity) {
        RLock redissonLock = redisson.getLock(RedissonLockKey.keyUnqid(prefix));
        redissonLock.lock(10, TimeUnit.SECONDS);

        try {
            SysUnqid bo = baseMapper.selectOne(new LambdaQueryWrapper<SysUnqid>().eq(SysUnqid::getPrefix, prefix));
            if (bo == null) {
                bo = new SysUnqid();
                bo.setId(IdUtil.getSnowflakeNextIdStr());
                bo.setSerialNumber(0);
                bo.setPrefix(prefix);
            }

            List<String> serialNumbers = new ArrayList<>();

            for (int i = 1; i <= quantity; i++) {
                serialNumbers.add(generateProcessor(prefix, numberLength, bo.getSerialNumber() + i));
            }
            bo.setSerialNumber(bo.getSerialNumber() + quantity);

            boolean saved = super.saveOrUpdate(bo);
            if (!saved) {
                throw new BusinessException(prefix + "流水号生成失败");
            }
            return serialNumbers;
        } finally {
            redissonLock.unlock();
        }
    }

    @Override
    public boolean exists(String prefix) {
        LambdaQueryWrapper<SysUnqid> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysUnqid::getId).eq(SysUnqid::getPrefix, prefix).last("limit 1");
        SysUnqid one = baseMapper.selectOne(queryWrapper);
        return one != null;
    }

    /**
     * 流水号生成加工
     *
     * @param prefix       流水号前缀
     * @param numberLength 流水号的序号长度
     * @param serialNumber 流水号的本次序号
     * @return 流水号
     */
    protected String generateProcessor(String prefix, Integer numberLength, Integer serialNumber) {
        StringBuilder codeSerialNumber = new StringBuilder(serialNumber.toString());

        if (codeSerialNumber.length() < numberLength) {
            // 补零
            return prefix + String.format("%06d", serialNumber);
        }

        return prefix + codeSerialNumber;
    }

}

