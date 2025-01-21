package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 系统-待办任务(SysPendingTasks)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-01-15 16:58:30
 */
@Data
public class SysPendingTasksQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 待办创建开始时间
     */
    private LocalDateTime startTimeFilter;

    /**
     * 待办创建结束时间
     */
    private LocalDateTime endTimeFilter;

    /**
     * 任务标题
     */
    private String taskTitle;

    /**
     * 关联业务主键Id
     */
    private String businessId;

    /**
     * 任务状态：0进行中，1已结束
     */
    private Integer taskStatus;

    /**
     * 任务结束时间
     */
    private LocalDateTime taskEndTime;

    /**
     * 任务创建人Id
     */
    private Long createId;

    /**
     * 任务节点
     */
    private String taskNode;

    /**
     * 任务标识
     */
    private String taskCode;


}

