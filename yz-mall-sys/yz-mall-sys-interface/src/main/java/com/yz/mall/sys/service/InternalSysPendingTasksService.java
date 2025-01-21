package com.yz.mall.sys.service;

import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author yunze
 * @date 2025/1/20 12:46
 */
public interface InternalSysPendingTasksService {

    /**
     * 开始任务
     *
     * @param taskInfo 任务信息
     * @return 任务Id
     */
    String startTask(@Valid InternalSysPendingTasksAddDto taskInfo);

    /**
     * 结束任务
     *
     * @param taskId 结束任务Id
     */
    boolean endTask(@NotNull Long taskId);
}
