package com.yz.unqid.dto;

import com.yz.unqid.entity.SysUnqid;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统-流水号表(SysUnqid)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
public class SerialNumberDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 完整流水号编号
     * 前缀+序号
     */
    private String code;

    /**
     * 流水号的序号
     * {@link SysUnqid#getSerialNumber()}
     */
    private Integer serialNumber;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public SerialNumberDto(String code, Integer serialNumber) {
        this.code = code;
        this.serialNumber = serialNumber;
    }
}

