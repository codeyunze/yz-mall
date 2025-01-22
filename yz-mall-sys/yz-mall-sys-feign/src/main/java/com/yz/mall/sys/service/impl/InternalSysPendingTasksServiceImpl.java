package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.sys.feign.InternalSysPendingTaskFeign;
import com.yz.mall.sys.service.InternalSysPendingTasksService;
import com.yz.mall.web.common.IdDto;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.enums.CodeEnum;
import com.yz.mall.web.exception.FeignException;
import org.springframework.stereotype.Service;

/**
 * 内部暴露service实现类: 任务待办
 *
 * @author yunze
 * @date 2025/1/22 13:46
 */
@Service
public class InternalSysPendingTasksServiceImpl implements InternalSysPendingTasksService {

    private final InternalSysPendingTaskFeign internalSysPendingTaskFeign;

    public InternalSysPendingTasksServiceImpl(InternalSysPendingTaskFeign internalSysPendingTaskFeign) {
        this.internalSysPendingTaskFeign = internalSysPendingTaskFeign;
    }

    @Override
    public Long startTask(InternalSysPendingTasksAddDto taskInfo) {
        Result<Long> result = internalSysPendingTaskFeign.startTask(taskInfo);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

    @Override
    public boolean endTask(Long taskId) {
        Result<Boolean> result = internalSysPendingTaskFeign.endTask(new IdDto(taskId));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }
}
