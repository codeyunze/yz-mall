package com.yz.search.business.systaskjobhistory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统-任务作业执行历史表
 * </p>
 *
 * @author yunze
 * @since 2022-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysTaskJobHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务主键ID(sys_task_job_plan[task_id])
     */
    private String taskId;

    /**
     * 任务开始时间
     */
    private LocalDateTime taskStartTime;

    /**
     * 任务结束时间
     */
    private LocalDateTime taskEndTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 执行状态：0-成功；1-失败
     */
    private Integer status;

    /**
     * 日志记录
     */
    private String log;

    /**
     * 任务执行地址
     */
    private String executeIp;


}
