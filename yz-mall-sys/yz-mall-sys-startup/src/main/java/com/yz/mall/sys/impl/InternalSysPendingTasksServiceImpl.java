package com.yz.mall.sys.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.sys.service.InternalSysPendingTasksService;
import com.yz.mall.sys.service.SysPendingTasksService;
import com.yz.mall.web.common.IdDto;
import org.springframework.stereotype.Service;

/**
 * @author yunze
 * @date 2025/1/20 12:50
 */
@Service
public class InternalSysPendingTasksServiceImpl implements InternalSysPendingTasksService {

    private final SysPendingTasksService sysPendingTasksService;

    public InternalSysPendingTasksServiceImpl(SysPendingTasksService sysPendingTasksService) {
        this.sysPendingTasksService = sysPendingTasksService;
    }

    @Override
    public String startTask(InternalSysPendingTasksAddDto taskInfo) {
        taskInfo.setCreateId(StpUtil.getLoginIdAsLong());
        Long id = sysPendingTasksService.save(taskInfo);
        return String.valueOf(id);
    }

    @Override
    public boolean endTask(Long taskId) {
        return sysPendingTasksService.end(new IdDto(taskId));
    }
}
