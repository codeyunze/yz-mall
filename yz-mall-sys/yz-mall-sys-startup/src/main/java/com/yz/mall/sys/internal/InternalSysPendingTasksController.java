package com.yz.mall.sys.internal;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.sys.service.InternalSysPendingTasksService;
import com.yz.mall.web.common.IdDto;
import com.yz.mall.web.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 内部暴露接口: 任务待办信息
 *
 * @author yunze
 * @date 2025/1/22 13:47
 */
@Slf4j
@RestController
@RequestMapping("internal/sys/tasks")
public class InternalSysPendingTasksController {

    private final InternalSysPendingTasksService internalSysPendingTasksService;

    public InternalSysPendingTasksController(InternalSysPendingTasksService internalSysPendingTasksService) {
        this.internalSysPendingTasksService = internalSysPendingTasksService;
    }

    /**
     * 开始任务
     *
     * @param taskInfo 任务信息
     * @return 任务Id
     */
    @PostMapping("startTask")
    public Result<Long> startTask(@RequestBody @Valid InternalSysPendingTasksAddDto taskInfo) {
        taskInfo.setCreateId(StpUtil.getLoginIdAsLong());
        boolean login = StpUtil.isLogin();
        log.info("用户是否登录：{}", login);
        Long taskId = internalSysPendingTasksService.startTask(taskInfo);
        return Result.success(taskId);
    }

    /**
     * 结束任务
     *
     * @param taskId 任务Id
     * @return 任务结束操作是否成功
     */
    @PostMapping("endTask")
    public Result<Boolean> endTask(@RequestBody @Valid IdDto taskId) {
        boolean ended = internalSysPendingTasksService.endTask(taskId.getId());
        return Result.success(ended);
    }
}
