package com.yz.mall.sys.service.impl;

import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.FeignException;
import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import com.yz.mall.sys.feign.ExtendSysPendingTaskFeign;
import com.yz.mall.sys.service.ExtendSysPendingTasksService;
import com.yz.mall.base.IdDto;
import com.yz.mall.base.Result;
import org.springframework.stereotype.Service;

/**
 * 内部暴露service实现类: 任务待办
 *
 * @author yunze
 * @date 2025/1/22 13:46
 */
@Service
public class ExtendSysPendingTasksServiceImpl implements ExtendSysPendingTasksService {

    private final ExtendSysPendingTaskFeign extendSysPendingTaskFeign;

    public ExtendSysPendingTasksServiceImpl(ExtendSysPendingTaskFeign extendSysPendingTaskFeign) {
        this.extendSysPendingTaskFeign = extendSysPendingTaskFeign;
    }

    @Override
    public Long startTask(ExtendSysPendingTasksAddDto taskInfo) {
        Result<Long> result = extendSysPendingTaskFeign.startTask(taskInfo);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

    @Override
    public boolean endTask(Long taskId) {
        Result<Boolean> result = extendSysPendingTaskFeign.endTask(new IdDto(taskId));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }
}
