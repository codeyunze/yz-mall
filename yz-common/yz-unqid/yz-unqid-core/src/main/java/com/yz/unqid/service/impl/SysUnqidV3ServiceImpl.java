package com.yz.unqid.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yz.advice.exception.BusinessException;
import com.yz.tools.RedisCacheKey;
import com.yz.tools.RedissonLockKey;
import com.yz.unqid.entity.SysUnqid;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 系统-序列号表(SysUnqid)表服务实现类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@Service("sysUnqidV3ServiceImpl")
public class SysUnqidV3ServiceImpl extends SysUnqidServiceImpl {

    @Override
    public String generateSerialNumber(String prefix, Integer numberLength) {
        // 加锁用于控制防止出现重复序列号情况
        RLock redissonLock = redisson.getLock(RedissonLockKey.keyUnqid(prefix));
        redissonLock.lock(10, TimeUnit.SECONDS);

        try {
            // 从缓存获取序列号对象信息
            SysUnqid bo = getSysUnqidPriorityCache(prefix);

            // 获取序列号的最新流水号，并更新缓存信息
            Integer serialNumber = updateSysUnqidCache(prefix, bo);

            if (serialNumber == null) {
                throw new BusinessException(prefix + "流水号生成失败");
            }

            String code = generateProcessor(prefix, numberLength, serialNumber);
            // baseMapper.record(code);
            return code;
        } finally {
            redissonLock.unlock();
        }
    }

    /**
     * 更新缓存的序列号对象信息
     *
     * @param prefix 序列号前缀
     * @param bo     序列号对象信息
     */
    private Integer updateSysUnqidCache(String prefix, SysUnqid bo) {
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(RedisCacheKey.objUnqid(prefix));
        if (bo == null) {
            bo = new SysUnqid();
            bo.setId(IdUtil.getSnowflakeNextIdStr());
            bo.setSerialNumber(1);
            bo.setPrefix(prefix);

            boundHashOps.put("id", bo.getId());
            boundHashOps.put("serialNumber", bo.getSerialNumber());
            boundHashOps.put("prefix", bo.getPrefix());
        } else {
            bo.setSerialNumber(bo.getSerialNumber() + 1);

            boundHashOps.put("serialNumber", bo.getSerialNumber());
        }

        return bo.getSerialNumber();
    }

    /**
     * 获取指定前缀的序列号对象信息-优先从缓存获取数据
     *
     * @param prefix 序列号前缀
     * @return 序列号前缀对应的序列号对象信息
     */
    private SysUnqid getSysUnqidPriorityCache(String prefix) {
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(RedisCacheKey.objUnqid(prefix));
        Object obj = boundHashOps.get("id");
        SysUnqid bo;
        if (obj == null) {
            // redis缓存里没有序列号数据，从mysql里查询，如果还没有，就直接返回空
            bo = baseMapper.selectOne(new LambdaQueryWrapper<SysUnqid>().eq(SysUnqid::getPrefix, prefix));
            if (bo == null) {
                return null;
            }
            boundHashOps.put("id", bo.getId());
            boundHashOps.put("serialNumber", bo.getSerialNumber());
            boundHashOps.put("prefix", bo.getPrefix());
        } else {
            // redis缓存里有序列号数据
            bo = new SysUnqid();
            bo.setId(obj.toString());
            bo.setSerialNumber((Integer) Objects.requireNonNull(boundHashOps.get("serialNumber")));
            bo.setPrefix((String) boundHashOps.get("prefix"));
        }
        return bo;
    }
}

