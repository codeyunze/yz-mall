package com.yz.mall.sys.feign;

import com.yz.mall.base.IdDto;
import com.yz.mall.base.Result;
import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import com.yz.mall.web.configuration.OpenFeignConfig;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 内部暴露接口: 任务待办
 *
 * @author yunze
 * @date 2025/1/22 14:06
 */
@FeignClient(name = "mall-sys", contextId = "extendSysPendingTask", path = "extend/sys/tasks", configuration = OpenFeignConfig.class)
public interface ExtendSysPendingTaskFeign {

    /**
     * 开始任务
     *
     * @param taskInfo 任务信息
     * @return 任务Id
     */
    @PostMapping("startTask")
    Result<Long> startTask(@RequestBody @Valid ExtendSysPendingTasksAddDto taskInfo);

    /**
     * 结束任务
     *
     * @param taskId 任务Id
     * @return 任务结束操作是否成功
     */
    @PostMapping("endTask")
    Result<Boolean> endTask(@RequestBody @Valid IdDto taskId);
}
