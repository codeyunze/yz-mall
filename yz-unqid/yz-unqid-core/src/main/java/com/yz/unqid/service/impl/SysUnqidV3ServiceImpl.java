package com.yz.unqid.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yz.mall.web.common.RedisCacheKey;
import com.yz.mall.web.common.RedissonLockKey;
import com.yz.unqid.UnqidHolder;
import com.yz.unqid.dto.SerialNumberDto;
import com.yz.unqid.entity.SysUnqid;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 系统-流水号表(SysUnqid)表服务实现类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@Service("sysUnqidV3ServiceImpl")
public class SysUnqidV3ServiceImpl extends SysUnqidServiceImpl {

    private final Integer numberPoolSize = 10000;

    @Override
    public String generateSerialNumber(String prefix, Integer numberLength) {
        // 直接从号池获取流水号
        SerialNumberDto serialNumberDto = UnqidHolder.get(prefix);
        if (serialNumberDto != null) {
            // baseMapper.record(serialNumberDto.getCode());
            return serialNumberDto.getCode();
        }

        // 加锁用于控制防止出现重复流水号情况
        RLock redissonLock = redisson.getLock(RedissonLockKey.keyUnqid(prefix));
        redissonLock.lock(10, TimeUnit.SECONDS);

        try {
            // 防止获取到锁之后，但是号池已经存在大量流水号的情况（防止出现如下场景：A和B两个线程等待锁，A先获取到了锁，线程A生成1000个流水号到号池，A释放锁，然后B获取到了锁，B再生成1000个流水号到号池，但此时号池里已经存在了A生成的1000个流水号）
            SerialNumberDto secondSerialNumberDto = UnqidHolder.get(prefix);
            if (secondSerialNumberDto != null) {
                return secondSerialNumberDto.getCode();
            }

            // 从缓存获取流水号对象信息
            SysUnqid bo = getSysUnqidPriorityCache(prefix);

            // 获取本批次第一个号的序号，并更新缓存信息
            int serialNumber = updateSysUnqidCache(prefix, bo);

            for (int i = 0; i < numberPoolSize; i++) {
                Integer currentSerialNumber = serialNumber + i;
                // 生成流水号
                String code = generateProcessor(prefix, numberLength, currentSerialNumber);
                // 存入流水号号池
                UnqidHolder.add(prefix, new SerialNumberDto(code, currentSerialNumber));
            }

            // 从号池获取流水号
            return Objects.requireNonNull(UnqidHolder.get(prefix)).getCode();
        } finally {
            redissonLock.unlock();
        }
    }

    /**
     * 更新缓存的流水号对象信息
     *
     * @param prefix 流水号前缀
     * @param bo     流水号对象信息
     * @return 本次初始序号（第一个流水号的序号）
     */
    private Integer updateSysUnqidCache(String prefix, SysUnqid bo) {
        BoundHashOperations<String, Object, Object> boundHashOps = defaultRedisTemplate.boundHashOps(RedisCacheKey.objUnqid(prefix));
        int currentInitialNumber;
        if (bo == null) {
            currentInitialNumber = 1;

            bo = new SysUnqid();
            bo.setId(IdUtil.getSnowflakeNextIdStr());
            bo.setSerialNumber(numberPoolSize);
            bo.setPrefix(prefix);

            boundHashOps.put("id", bo.getId());
            boundHashOps.put("serialNumber", bo.getSerialNumber());
            boundHashOps.put("prefix", bo.getPrefix());
        } else {
            currentInitialNumber = bo.getSerialNumber() + 1;

            bo.setSerialNumber(bo.getSerialNumber() + numberPoolSize);

            boundHashOps.put("serialNumber", bo.getSerialNumber());
        }
        return currentInitialNumber;
    }

    /**
     * 获取指定前缀的流水号对象信息-优先从缓存获取数据
     *
     * @param prefix 流水号前缀
     * @return 流水号前缀对应的对象信息
     */
    private SysUnqid getSysUnqidPriorityCache(String prefix) {
        BoundHashOperations<String, Object, Object> boundHashOps = defaultRedisTemplate.boundHashOps(RedisCacheKey.objUnqid(prefix));
        Object obj = boundHashOps.get("id");
        SysUnqid bo;
        if (obj == null) {
            // redis缓存里没有流水号数据，从mysql里查询，如果还没有，就直接返回空
            bo = baseMapper.selectOne(new LambdaQueryWrapper<SysUnqid>().eq(SysUnqid::getPrefix, prefix));
            if (bo == null) {
                return null;
            }
            boundHashOps.put("id", bo.getId());
            boundHashOps.put("serialNumber", bo.getSerialNumber());
            boundHashOps.put("prefix", bo.getPrefix());
        } else {
            // redis缓存里有流水号数据
            bo = new SysUnqid();
            bo.setId(obj.toString());
            bo.setSerialNumber((Integer) Objects.requireNonNull(boundHashOps.get("serialNumber")));
            bo.setPrefix((String) boundHashOps.get("prefix"));
        }
        return bo;
    }
}

