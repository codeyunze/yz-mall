package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 系统-待办任务(SysPendingTasks)表更新数据模型类
 *
 * @author yunze
 * @since 2025-01-15 16:58:30
 */
@Data
public class SysPendingTasksUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 任务标题
     */
    private String taskTitle;
}

