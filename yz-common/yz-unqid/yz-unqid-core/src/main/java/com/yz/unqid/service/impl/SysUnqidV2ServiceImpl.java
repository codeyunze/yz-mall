package com.yz.unqid.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.advice.exception.BusinessException;
import com.yz.tools.RedissonLockKey;
import com.yz.unqid.entity.SysUnqid;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 系统-序列号表(SysUnqid)表服务实现类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@Service("sysUnqidV2ServiceImpl")
public class SysUnqidV2ServiceImpl extends SysUnqidServiceImpl {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
        if (bo == null) {
            bo = new SysUnqid();
            bo.setId(IdUtil.getSnowflakeNextIdStr());
            bo.setSerialNumber(1);
            bo.setPrefix(prefix);
        } else {
            bo.setSerialNumber(bo.getSerialNumber() + 1);
        }
        redisTemplate.boundHashOps(RedissonLockKey.objUnqid(prefix)).put(prefix, bo);
        return bo.getSerialNumber();
    }

    /**
     * 获取指定前缀的序列号对象信息-优先从缓存获取数据
     *
     * @param prefix 序列号前缀
     * @return 序列号前缀对应的序列号对象信息
     */
    private SysUnqid getSysUnqidPriorityCache(String prefix) {
        Object obj = redisTemplate.boundHashOps(RedissonLockKey.objUnqid(prefix)).get(prefix);
        SysUnqid bo;
        if (obj == null) {
            bo = baseMapper.selectOne(new LambdaQueryWrapper<SysUnqid>().eq(SysUnqid::getPrefix, prefix));
            if (bo != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    redisTemplate.boundHashOps(RedissonLockKey.objUnqid(prefix)).put(prefix, objectMapper.writeValueAsString(bo));
                } catch (JsonProcessingException e) {
                    throw new BusinessException(e.getMessage());
                }
            }
        } else {
            bo = (SysUnqid) obj;
        }
        return bo;
    }
}

