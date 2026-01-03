package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.redis.RedissonLockKey;
import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import com.yz.mall.sys.dto.MsgRetryRecordDto;
import com.yz.mall.sys.entity.SysMsgRetry;
import com.yz.mall.sys.enums.MsgRetryStatusEnum;
import com.yz.mall.sys.mapper.SysMsgRetryMapper;
import com.yz.mall.sys.service.SysMsgRetryService;
import com.yz.mall.sys.service.SysPendingTasksService;
import com.yz.mall.sys.utils.ExceptionRetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 消息重试记录表(SysMsgRetry)表服务实现类
 *
 * @author yunze
 * @since 2025-01-20
 */
@Slf4j
@Service
public class SysMsgRetryServiceImpl extends ServiceImpl<SysMsgRetryMapper, SysMsgRetry> implements SysMsgRetryService {

    private static final int DEFAULT_MAX_RETRY_COUNT = 5;
    private static final int RETRY_DELAY_BASE_MS = 1000; // 基础延迟1秒

    private final SysPendingTasksService sysPendingTasksService;
    private final Redisson redisson;

    public SysMsgRetryServiceImpl(SysPendingTasksService sysPendingTasksService, Redisson redisson) {
        this.sysPendingTasksService = sysPendingTasksService;
        this.redisson = redisson;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long recordFailure(MsgRetryRecordDto dto) {
        // 检查是否已存在（幂等性）
        SysMsgRetry existing = getByMsgId(dto.getMsgId());
        if (existing != null) {
            log.warn("消息已存在，msgId: {}, status: {}", dto.getMsgId(), existing.getStatus());
            return existing.getId();
        }

        boolean retryable = dto.getRetryable() != null && dto.getRetryable();

        SysMsgRetry msgRetry = new SysMsgRetry();
        msgRetry.setId(IdUtil.getSnowflakeNextId());
        msgRetry.setMsgId(dto.getMsgId());
        msgRetry.setTopic(dto.getTopic());
        msgRetry.setTags(dto.getTags());
        msgRetry.setBody(dto.getBody());
        msgRetry.setConsumerGroup(dto.getConsumerGroup());
        msgRetry.setBusinessId(dto.getBusinessId());
        msgRetry.setRetryCount(retryable ? DEFAULT_MAX_RETRY_COUNT : 0);
        msgRetry.setStatus(retryable ? MsgRetryStatusEnum.RETRYING.get() : MsgRetryStatusEnum.PENDING.get());
        msgRetry.setException(getExceptionString(dto.getException()));
        msgRetry.setVersion(0);

        // 如果可重试，设置下次重试时间（指数退避）
        if (retryable) {
            msgRetry.setNextRetryTime(LocalDateTime.now().plusSeconds(RETRY_DELAY_BASE_MS / 1000));
        }

        baseMapper.insert(msgRetry);
        log.info("记录消息消费失败，msgId: {}, retryable: {}", dto.getMsgId(), retryable);
        return msgRetry.getId();
    }

    @Override
    public List<SysMsgRetry> queryRetryableMessages(int limit) {
        LambdaQueryWrapper<SysMsgRetry> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMsgRetry::getStatus, MsgRetryStatusEnum.RETRYING.get())
                .gt(SysMsgRetry::getRetryCount, 0)
                .le(SysMsgRetry::getNextRetryTime, LocalDateTime.now())
                .orderByAsc(SysMsgRetry::getNextRetryTime)
                .last("LIMIT " + limit);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean retryMessage(SysMsgRetry msgRetry) {
        // 使用分布式锁，防止并发重试
        RLock lock = redisson.getLock(RedissonLockKey.keyMsgRetry(msgRetry.getId()));
        try {
            if (!lock.tryLock(5, TimeUnit.SECONDS)) {
                log.warn("获取重试锁失败，msgRetryId: {}", msgRetry.getId());
                return false;
            }

            try {
                // 重新查询，使用乐观锁
                SysMsgRetry current = baseMapper.selectById(msgRetry.getId());
                if (current == null || !current.getStatus().equals(MsgRetryStatusEnum.RETRYING.get())
                        || current.getRetryCount() <= 0) {
                    log.warn("消息状态已变更，无需重试，msgRetryId: {}", msgRetry.getId());
                    return false;
                }

                // 执行重试
                boolean success = doRetry(current);

                // 更新重试历史
                updateRetryHistory(current, success);

                if (success) {
                    // 重试成功，标记为已处理
                    markAsProcessed(current.getId());
                    log.info("消息重试成功，msgRetryId: {}, msgId: {}", current.getId(), current.getMsgId());
                } else {
                    // 重试失败，减少重试次数
                    int newRetryCount = current.getRetryCount() - 1;
                    if (newRetryCount > 0) {
                        // 还有重试次数，更新下次重试时间（指数退避）
                        long delaySeconds = (long) Math.pow(2, DEFAULT_MAX_RETRY_COUNT - newRetryCount) * (RETRY_DELAY_BASE_MS / 1000);
                        LambdaUpdateWrapper<SysMsgRetry> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(SysMsgRetry::getId, current.getId())
                                .set(SysMsgRetry::getRetryCount, newRetryCount)
                                .set(SysMsgRetry::getNextRetryTime, LocalDateTime.now().plusSeconds(delaySeconds))
                                .eq(SysMsgRetry::getVersion, current.getVersion())
                                .set(SysMsgRetry::getVersion, current.getVersion() + 1);
                        baseMapper.update(null, updateWrapper);
                        log.info("消息重试失败，剩余重试次数: {}, msgRetryId: {}", newRetryCount, current.getId());
                    } else {
                        // 重试次数用完，标记为待处理
                        LambdaUpdateWrapper<SysMsgRetry> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(SysMsgRetry::getId, current.getId())
                                .set(SysMsgRetry::getStatus, MsgRetryStatusEnum.PENDING.get())
                                .set(SysMsgRetry::getRetryCount, 0)
                                .eq(SysMsgRetry::getVersion, current.getVersion())
                                .set(SysMsgRetry::getVersion, current.getVersion() + 1);
                        baseMapper.update(null, updateWrapper);
                        log.warn("消息重试次数已用完，标记为待处理，msgRetryId: {}", current.getId());
                    }
                }

                return success;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取重试锁被中断，msgRetryId: {}", msgRetry.getId(), e);
            return false;
        }
    }

    /**
     * 执行实际的重试逻辑
     */
    private boolean doRetry(SysMsgRetry msgRetry) {
        try {
            // 解析消息体
            ExtendSysPendingTasksAddDto dto = JacksonUtil.getObjectMapper().readValue(msgRetry.getBody(), ExtendSysPendingTasksAddDto.class);
            // 执行业务逻辑
            Long id = sysPendingTasksService.save(dto);
            return id != null;
        } catch (JsonProcessingException e) {
            log.error("解析消息体失败，msgRetryId: {}", msgRetry.getId(), e);
            return false;
        } catch (Exception e) {
            log.error("消息重试执行失败，msgRetryId: {}", msgRetry.getId(), e);
            return false;
        }
    }

    /**
     * 更新重试历史
     */
    private void updateRetryHistory(SysMsgRetry msgRetry, boolean success) {
        try {
            List<Map<String, Object>> history = new ArrayList<>();
            if (msgRetry.getRetryHistory() != null) {
                history = JacksonUtil.getObjectMapper().readValue(msgRetry.getRetryHistory(), List.class);
            }

            Map<String, Object> retryRecord = new HashMap<>();
            retryRecord.put("attempt", DEFAULT_MAX_RETRY_COUNT - msgRetry.getRetryCount() + 1);
            retryRecord.put("operation_time", LocalDateTime.now().toString());
            retryRecord.put("exception_type", success ? null : "RetryException");
            retryRecord.put("error_code", null);
            retryRecord.put("error_message", success ? "重试成功" : "重试失败");
            retryRecord.put("consumer_ip", getLocalIp());
            retryRecord.put("result", success ? "SUCCESS" : "FAILED");

            history.add(retryRecord);

            // 限制历史记录数量，最多保留20条
            if (history.size() > 20) {
                history = history.subList(history.size() - 20, history.size());
            }

            String historyJson = JacksonUtil.getObjectMapper().writeValueAsString(history);
            LambdaUpdateWrapper<SysMsgRetry> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysMsgRetry::getId, msgRetry.getId())
                    .set(SysMsgRetry::getRetryHistory, historyJson);
            baseMapper.update(null, updateWrapper);
        } catch (Exception e) {
            log.error("更新重试历史失败，msgRetryId: {}", msgRetry.getId(), e);
        }
    }

    @Override
    public SysMsgRetry getByMsgId(String msgId) {
        LambdaQueryWrapper<SysMsgRetry> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMsgRetry::getMsgId, msgId)
                .last("LIMIT 1");
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsProcessed(Long id) {
        LambdaUpdateWrapper<SysMsgRetry> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysMsgRetry::getId, id)
                .set(SysMsgRetry::getStatus, MsgRetryStatusEnum.PROCESSED.get());
        return baseMapper.update(null, updateWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsIgnored(Long id) {
        LambdaUpdateWrapper<SysMsgRetry> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysMsgRetry::getId, id)
                .set(SysMsgRetry::getStatus, MsgRetryStatusEnum.IGNORED.get());
        return baseMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 获取异常信息字符串
     */
    private String getExceptionString(Throwable exception) {
        if (exception == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 获取本机IP地址
     */
    private String getLocalIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}

