package com.yz.mall.sys.extend;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.base.IdDto;
import com.yz.mall.base.Result;
import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.sys.service.ExtendSysPendingTasksService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 内部暴露接口: 任务待办信息
 *
 * @author yunze
 * @date 2025/1/22 13:47
 */
@Slf4j
@RestController
@RequestMapping("extend/sys/tasks")
public class ExtendSysPendingTasksController {

    private final ExtendSysPendingTasksService extendSysPendingTasksService;

    public ExtendSysPendingTasksController(ExtendSysPendingTasksService extendSysPendingTasksService) {
        this.extendSysPendingTasksService = extendSysPendingTasksService;
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
        Long taskId = extendSysPendingTasksService.startTask(taskInfo);
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
        boolean ended = extendSysPendingTasksService.endTask(taskId.getId());
        return Result.success(ended);
    }
}
