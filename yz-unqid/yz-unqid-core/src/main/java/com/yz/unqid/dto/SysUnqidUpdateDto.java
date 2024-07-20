package com.yz.unqid.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统-序列号表(SysUnqid)表更新数据模型类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
public class SysUnqidUpdateDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    @NotBlank(message = "主键标识不能为空")
    private String id;

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

