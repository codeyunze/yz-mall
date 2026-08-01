package com.yz.mall.sys.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * 按业务主键结束待办
 */
@Data
public class ExtendSysPendingTasksEndDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联业务主键Id
     */
    @NotBlank(message = "关联业务主键Id不能为空")
    @Length(max = 36, message = "关联业务主键Id不能超过36个字符")
    private String businessId;

    /**
     * 任务标识
     */
    @NotBlank(message = "任务标识不能为空")
    @Length(max = 36, message = "任务标识不能超过36个字符")
    private String taskCode;
}
