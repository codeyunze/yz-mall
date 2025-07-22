package com.yz.mall.serial.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统-流水号表(SysUnqid)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@Data
public class SerialUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotBlank(message = "主键标识不能为空")
    private String id;

    /**
     * 流水号前缀
     */
    private String prefix;

    /**
     * 流水号的序号
     */
    private Integer serialNumber;

}

