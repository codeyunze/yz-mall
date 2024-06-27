package com.yz.unqid.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统-序列号表(SysUnqid)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
public class SysUnqidQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    private String id;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 序号前缀
     */
    private String prefix;

    /**
     * 序列号
     */
    private Integer serialNumber;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
}

