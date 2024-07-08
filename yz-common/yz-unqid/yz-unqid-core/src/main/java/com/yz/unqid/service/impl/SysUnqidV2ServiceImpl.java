package com.yz.unqid.service.impl;

import com.yz.tools.RedissonLockKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
        Object unqid = redisTemplate.boundHashOps(RedissonLockKey.objUnqid(prefix)).get(prefix);

        return "";
    }
}

