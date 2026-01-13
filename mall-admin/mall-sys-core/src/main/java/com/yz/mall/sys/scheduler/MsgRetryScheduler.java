package com.yz.mall.sys.scheduler;

import com.yz.mall.redis.RedissonLockKey;
import com.yz.mall.sys.entity.SysMsgRetry;
import com.yz.mall.sys.service.SysMsgRetryService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 消息重试定时任务
 *
 * @author yunze
 * @since 2025-01-20
 */
@Slf4j
@Component
public class MsgRetryScheduler {

    private static final int BATCH_SIZE = 100; // 每次处理的消息数量

    private final SysMsgRetryService sysMsgRetryService;
    private final Redisson redisson;

    public MsgRetryScheduler(SysMsgRetryService sysMsgRetryService, Redisson redisson) {
        this.sysMsgRetryService = sysMsgRetryService;
        this.redisson = redisson;
    }

    /**
     * 定时扫描并重试消息
     * 每30秒执行一次
     */
    @Scheduled(fixedDelay = 30000)
    public void retryMessages() {
        // 使用分布式锁，确保多实例部署时只有一个实例执行
        RLock lock = redisson.getLock(RedissonLockKey.keyMsgRetryScheduler());
        try {
            if (!lock.tryLock(5, TimeUnit.SECONDS)) {
                log.debug("获取重试任务锁失败，可能其他实例正在执行");
                return;
            }

            try {
                // 查询待重试的消息
                List<SysMsgRetry> retryableMessages = sysMsgRetryService.queryRetryableMessages(BATCH_SIZE);
                if (retryableMessages.isEmpty()) {
                    log.debug("没有待重试的消息");
                    return;
                }

                log.info("开始重试消息，数量: {}", retryableMessages.size());

                int successCount = 0;
                int failCount = 0;

                for (SysMsgRetry msgRetry : retryableMessages) {
                    try {
                        boolean success = sysMsgRetryService.retryMessage(msgRetry);
                        if (success) {
                            successCount++;
                        } else {
                            failCount++;
                        }
                    } catch (Exception e) {
                        log.error("重试消息异常，msgRetryId: {}", msgRetry.getId(), e);
                        failCount++;
                    }
                }

                log.info("消息重试完成，成功: {}, 失败: {}", successCount, failCount);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取重试任务锁被中断", e);
        } catch (Exception e) {
            log.error("消息重试任务执行异常", e);
        }
    }
}

