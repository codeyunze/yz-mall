package com.yz.mall.sys.service.impl;

import com.yz.mall.base.IdDto;
import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.sys.service.ExtendSysPendingTasksService;
import com.yz.mall.sys.service.SysPendingTasksService;
import org.springframework.stereotype.Service;

/**
 * @author yunze
 * @date 2025/1/20 12:50
 */
@Service
public class ExtendSysPendingTasksServiceImpl implements ExtendSysPendingTasksService {

    private final SysPendingTasksService sysPendingTasksService;

    public ExtendSysPendingTasksServiceImpl(SysPendingTasksService sysPendingTasksService) {
        this.sysPendingTasksService = sysPendingTasksService;
    }

    @Override
    public Long startTask(InternalSysPendingTasksAddDto taskInfo) {
        return sysPendingTasksService.save(taskInfo);
    }

    @Override
    public boolean endTask(Long taskId) {
        return sysPendingTasksService.end(new IdDto(taskId));
    }
}
