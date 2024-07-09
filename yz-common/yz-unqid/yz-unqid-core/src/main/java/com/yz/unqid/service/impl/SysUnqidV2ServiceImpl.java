package com.yz.unqid.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yz.advice.exception.BusinessException;
import com.yz.tools.RedissonLockKey;
import com.yz.unqid.entity.SysUnqid;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
            BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(RedissonLockKey.objUnqid(prefix));
            Object obj = boundHashOps.get(prefix);
            SysUnqid bo;
            if (obj == null) {
                bo = baseMapper.selectOne(new LambdaQueryWrapper<SysUnqid>().eq(SysUnqid::getPrefix, prefix));
                if (bo != null) {
                    boundHashOps.put(prefix, bo);
                }
            } else {
                bo = (SysUnqid) obj;
            }

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
            boundHashOps.put(prefix, bo);

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
}

