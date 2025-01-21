package com.yz.mall.sys.demo;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yunze
 * @date 2025/1/20 15:05
 */
@RestController
@RequestMapping(value = "/sys/task")
public class TaskController {

    @Resource
    private TaskService taskService;

    @SaIgnore
    @RequestMapping("/start/{taskCode}")
    public String start(@PathVariable String taskCode) {
        return taskService.start(taskCode);
    }

    @SaIgnore
    @RequestMapping("/end/{taskCode}")
    public void end(@PathVariable String taskCode) {
        taskService.end(taskCode, "123");
    }
}
