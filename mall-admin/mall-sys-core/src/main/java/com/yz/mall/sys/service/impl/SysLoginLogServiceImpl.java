package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.sys.dto.SysLoginLogQueryDto;
import com.yz.mall.sys.entity.SysLoginLog;
import com.yz.mall.sys.mapper.SysLoginLogMapper;
import com.yz.mall.sys.service.SysLoginLogService;
import com.yz.mall.sys.vo.SysLoginLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统-登录日志(SysLoginLog)表服务实现类
 *
 * @author yunze
 * @since 2025-12-11
 */
@Slf4j
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    private final PlatformTransactionManager transactionManager;

    public SysLoginLogServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Page<SysLoginLogVo> page(PageFilter<SysLoginLogQueryDto> filter) {
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
    }

    @Override
    public void recordLoginLog(String username, String loginIp, String loginLocation, String os, String browser, Integer status, Integer loginType) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setId(IdUtil.getSnowflakeNextId());
            loginLog.setUsername(username);
            loginLog.setLoginIp(loginIp);
            loginLog.setLoginLocation(loginLocation);
            loginLog.setOs(os);
            loginLog.setBrowser(browser);
            loginLog.setStatus(status);
            loginLog.setLoginType(loginType);
            loginLog.setLoginTime(LocalDateTime.now());
            loginLog.setCreateTime(LocalDateTime.now());
            loginLog.setUpdateTime(LocalDateTime.now());
            baseMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }

    /**
     * 清空日志（分批处理，避免事务过大）
     * <p>
     * 每次处理 10000 条数据，循环执行直到没有更多数据需要清理
     * 每次处理在独立的事务中完成，避免长时间锁定表
     * <p>
     * 使用 TransactionTemplate 手动管理事务，避免自调用导致事务不生效的问题
     *
     * @return 是否操作成功
     */
    @Override
    public boolean clearLogs() {
        int batchSize = 100;
        int totalCleared = 0;
        int batchCount = 0;
        int maxBatches = 1000; // 最大批次限制，防止无限循环（最多清理 1000万条）

        log.info("开始清理登录日志，每批处理 {} 条", batchSize);

        while (batchCount < maxBatches) {
            int cleared = clearLogsBatch(batchSize);
            if (cleared == 0) {
                // 没有更多数据需要清理，退出循环
                break;
            }
            totalCleared += cleared;
            batchCount++;
            log.debug("第 {} 批清理完成，本次清理 {} 条，累计清理 {} 条", batchCount, cleared, totalCleared);

            // 如果本次清理的数量小于批次大小，说明已经是最后一批了
            if (cleared < batchSize) {
                break;
            }
        }

        log.info("登录日志清理完成，共处理 {} 批，累计清理 {} 条", batchCount, totalCleared);
        return totalCleared > 0;
    }

    /**
     * 单批清理日志（在独立事务中执行）
     * <p>
     * 先查询需要清理的 ID 列表，然后根据 ID 列表进行更新
     * 使用 TransactionTemplate 手动管理事务，确保每个批次都在独立事务中执行
     *
     * @param batchSize 批次大小
     * @return 本次清理的数量
     */
    private int clearLogsBatch(int batchSize) {
        // 配置事务：使用 REQUIRES_NEW 传播行为，确保每个批次都在独立事务中执行
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {
            // 先查询需要清理的 ID 列表（只查询有效数据）
            LambdaQueryWrapper<SysLoginLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(SysLoginLog::getId);
            queryWrapper.eq(SysLoginLog::getInvalid, 0);
            queryWrapper.orderByAsc(SysLoginLog::getId);
            queryWrapper.last("LIMIT " + batchSize);

            List<SysLoginLog> logsToClear = baseMapper.selectList(queryWrapper);

            if (logsToClear.isEmpty()) {
                transactionManager.commit(transactionStatus);
                return 0;
            }

            // 提取 ID 列表
            List<Long> ids = logsToClear.stream()
                    .map(SysLoginLog::getId)
                    .collect(Collectors.toList());

            // 根据 ID 列表批量更新
            LambdaUpdateWrapper<SysLoginLog> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(SysLoginLog::getId, ids);
            updateWrapper.set(SysLoginLog::getInvalid, 1);

            int updated = baseMapper.update(null, updateWrapper);

            // 提交事务
            transactionManager.commit(transactionStatus);
            return updated;
        } catch (Exception e) {
            // 回滚事务
            transactionManager.rollback(transactionStatus);
            log.error("清理登录日志失败，批次大小: {}", batchSize, e);
            throw e;
        }
    }
}

