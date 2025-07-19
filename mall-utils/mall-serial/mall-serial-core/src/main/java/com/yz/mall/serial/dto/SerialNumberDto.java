package com.yz.mall.serial.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 系统-流水号表(SysUnqid)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@Data
public class SerialNumberDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 完整流水号编号
     * 前缀+序号
     */
    private String code;

    /**
     * 流水号的序号
     * {@link com.yz.mall.serial.entity.SysUnqid#getSerialNumber()}
     */
    private Integer serialNumber;

    public SerialNumberDto() {
    }

    public SerialNumberDto(String code, Integer serialNumber) {
        this.code = code;
        this.serialNumber = serialNumber;
    }
}

