package com.yz.mall.sys.feign;

import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.web.common.IdDto;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 内部暴露接口: 任务待办
 *
 * @author yunze
 * @date 2025/1/22 14:06
 */
@FeignClient(name = "yz-mall-sys", contextId = "internalSysPendingTask", path = "internal/sys/tasks", configuration = FeignConfig.class)
public interface InternalSysPendingTaskFeign {

    /**
     * 开始任务
     *
     * @param taskInfo 任务信息
     * @return 任务Id
     */
    @PostMapping("startTask")
    Result<Long> startTask(@RequestBody @Valid InternalSysPendingTasksAddDto taskInfo);

    /**
     * 结束任务
     *
     * @param taskId 任务Id
     * @return 任务结束操作是否成功
     */
    @PostMapping("endTask")
    Result<Boolean> endTask(@RequestBody @Valid IdDto taskId);
}
