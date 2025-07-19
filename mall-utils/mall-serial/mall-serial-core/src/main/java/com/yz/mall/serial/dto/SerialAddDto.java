package com.yz.mall.serial.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统-流水号表(SysUnqid)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@Data
public class SerialAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流水号前缀
     */
    @NotBlank(message = "流水号前缀不能为空")
    private String prefix;

    /**
     * 流水号的序号
     */
    @NotBlank(message = "流水号序号不能为空")
    private Integer serialNumber;
}

