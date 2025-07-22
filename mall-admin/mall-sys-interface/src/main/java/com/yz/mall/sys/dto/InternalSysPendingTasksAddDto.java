package com.yz.mall.sys.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统-待办任务(SysPendingTasks)表新增数据模型类
 *
 * @author yunze
 * @since 2025-01-15 16:58:30
 */
@Data
public class InternalSysPendingTasksAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务标题
     */
    @NotBlank(message = "任务标题不能为空")
    @Length(max = 36, message = "任务标题不能超过36个字符")
    private String taskTitle;

    /**
     * 关联业务主键Id
     */
    @NotBlank(message = "关联业务主键Id不能为空")
    @Length(max = 36, message = "关联业务主键Id不能超过36个字符")
    private String businessId;

    /**
     * 任务节点
     */
    @NotBlank(message = "任务节点不能为空")
    @Length(max = 36, message = "任务节点不能超过36个字符")
    private String taskNode;

    /**
     * 任务标识
     */
    @NotBlank(message = "任务标识不能为空")
    @Length(max = 36, message = "任务标识不能超过36个字符")
    private String taskCode;

    /**
     * 任务创建人Id
     * @ignore
     */
    private Long createId;
}

