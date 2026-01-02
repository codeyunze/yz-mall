package com.yz.mall.sys.service;

import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


/**
 * @author yunze
 * @date 2025/1/20 12:46
 */
public interface ExtendSysPendingTasksService {

    /**
     * 开始任务
     * 模式一: service实现类引入的是core包，则是待办数据直接入库
     * 模式二: service实现类引入的是feign包，则通过feign发送rocketmq消息，由core包消费消息，待办数据入库
     *
     * @param taskInfo 任务信息
     * @return 任务Id
     */
    Long startTask(@Valid ExtendSysPendingTasksAddDto taskInfo);

    /**
     * 结束任务
     *
     * @param taskId 结束任务Id
     */
    boolean endTask(@NotNull Long taskId);
}
