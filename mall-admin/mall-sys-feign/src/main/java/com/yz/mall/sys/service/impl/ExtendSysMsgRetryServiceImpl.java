package com.yz.mall.sys.service.impl;

import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.FeignException;
import com.yz.mall.sys.dto.MsgRetryRecordDto;
import com.yz.mall.sys.feign.ExtendSysMsgRetryFeign;
import com.yz.mall.sys.service.ExtendSysMsgRetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 消息重试服务扩展接口实现类（Feign调用）
 * 供其他模块使用，通过 Feign 调用 mall-sys 服务
 *
 * @author yunze
 * @since 2025-01-20
 */
@Slf4j
@Service
public class ExtendSysMsgRetryServiceImpl implements ExtendSysMsgRetryService {

    private final ExtendSysMsgRetryFeign extendSysMsgRetryFeign;

    public ExtendSysMsgRetryServiceImpl(ExtendSysMsgRetryFeign extendSysMsgRetryFeign) {
        this.extendSysMsgRetryFeign = extendSysMsgRetryFeign;
    }

    @Override
    public Long recordFailure(MsgRetryRecordDto dto) {
        Result<Long> result = extendSysMsgRetryFeign.recordFailure(dto);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            log.error("记录消息消费失败失败，msgId: {}, error: {}", dto.getMsgId(), result.getMsg());
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

    @Override
    public Integer getStatusByMsgId(String msgId) {
        Result<Integer> result = extendSysMsgRetryFeign.getStatusByMsgId(msgId);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            log.error("查询消息状态失败，msgId: {}, error: {}", msgId, result.getMsg());
            return null;
        }
        return result.getData();
    }

    @Override
    public boolean markAsProcessedByMsgId(String msgId) {
        Result<Boolean> result = extendSysMsgRetryFeign.markAsProcessedByMsgId(msgId);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            log.error("标记消息为已处理失败，msgId: {}, error: {}", msgId, result.getMsg());
            return false;
        }
        return Boolean.TRUE.equals(result.getData());
    }
}
