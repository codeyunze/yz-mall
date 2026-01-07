package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.MsgRetryRecordDto;
import com.yz.mall.sys.dto.SysMsgRetryQueryDto;
import com.yz.mall.sys.entity.SysMsgRetry;

import java.util.List;

/**
 * 消息重试记录表(SysMsgRetry)表服务接口
 *
 * @author yunze
 * @since 2025-01-20
 */
public interface SysMsgRetryService extends IService<SysMsgRetry> {

    /**
     * 记录消息消费失败
     *
     * @param dto 消息重试记录DTO
     * @return 记录ID
     */
    Long recordFailure(MsgRetryRecordDto dto);

    /**
     * 查询待重试的消息
     *
     * @param limit 查询数量限制
     * @return 待重试消息列表
     */
    List<SysMsgRetry> queryRetryableMessages(int limit);

    /**
     * 执行消息重试
     *
     * @param msgRetry 消息重试记录
     * @return 是否重试成功
     */
    boolean retryMessage(SysMsgRetry msgRetry);

    /**
     * 根据消息ID查询记录（用于幂等性判断）
     *
     * @param msgId 消息ID
     * @return 消息重试记录
     */
    SysMsgRetry getByMsgId(String msgId);

    /**
     * 标记消息为已处理
     *
     * @param id 记录ID
     * @return 是否成功
     */
    boolean markAsProcessed(Long id);

    /**
     * 标记消息为忽略
     *
     * @param id 记录ID
     * @return 是否成功
     */
    boolean markAsIgnored(Long id);

    /**
     * 分页查询消息重试记录
     *
     * @param current 当前页
     * @param size    每页大小
     * @param queryDto 查询条件
     * @return 分页结果
     */
    IPage<SysMsgRetry> page(long current, long size, SysMsgRetryQueryDto queryDto);
}

