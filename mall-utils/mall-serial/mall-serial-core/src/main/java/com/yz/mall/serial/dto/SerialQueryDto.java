package com.yz.mall.serial.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统-流水号表(SysUnqid)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@Data
public class SerialQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private String id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 流水号前缀
     */
    private String prefix;

    /**
     * 序列号
     */
    private Integer serialNumber;
}

