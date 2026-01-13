package com.yz.mall.sys.service;

import com.yz.mall.sys.dto.MsgRetryRecordDto;

/**
 * 消息重试服务扩展接口
 * 供其他模块使用，不依赖具体实现
 *
 * @author yunze
 * @since 2025-01-20
 */
public interface ExtendSysMsgRetryService {

    /**
     * 记录消息消费失败
     *
     * @param dto 消息重试记录DTO
     * @return 记录ID
     */
    Long recordFailure(MsgRetryRecordDto dto);

    /**
     * 根据消息ID查询记录状态（用于幂等性判断）
     *
     * @param msgId 消息ID
     * @return 消息重试记录状态码（0重试中/1待处理/2已处理/3忽略），如果不存在返回null
     */
    Integer getStatusByMsgId(String msgId);

    /**
     * 根据消息ID标记消息为已处理
     *
     * @param msgId 消息ID
     * @return 是否成功
     */
    boolean markAsProcessedByMsgId(String msgId);
}

