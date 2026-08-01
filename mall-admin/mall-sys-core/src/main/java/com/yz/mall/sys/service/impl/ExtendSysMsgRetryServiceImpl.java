package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.dto.MsgRetryRecordDto;
import com.yz.mall.sys.entity.SysMsgRetry;
import com.yz.mall.sys.enums.MsgRetryStatusEnum;
import com.yz.mall.sys.service.ExtendSysMsgRetryService;
import com.yz.mall.sys.service.SysMsgRetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 消息重试服务扩展接口实现类
 *
 * @author yunze
 * @since 2025-01-20
 */
@Slf4j
@Service
public class ExtendSysMsgRetryServiceImpl implements ExtendSysMsgRetryService {

    private final SysMsgRetryService sysMsgRetryService;

    public ExtendSysMsgRetryServiceImpl(SysMsgRetryService sysMsgRetryService) {
        this.sysMsgRetryService = sysMsgRetryService;
    }

    @Override
    public Long recordFailure(MsgRetryRecordDto dto) {
        return sysMsgRetryService.recordFailure(dto);
    }

    @Override
    public Integer getStatusByMsgId(String msgId) {
        SysMsgRetry msgRetry = sysMsgRetryService.getByMsgId(msgId);
        return msgRetry != null ? msgRetry.getStatus() : null;
    }

    @Override
    public boolean markAsProcessedByMsgId(String msgId) {
        SysMsgRetry msgRetry = sysMsgRetryService.getByMsgId(msgId);
        if (msgRetry != null) {
            return sysMsgRetryService.markAsProcessed(msgRetry.getId());
        }
        return false;
    }
}

