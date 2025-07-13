package com.yz.unqid.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统-流水号表(SysUnqid)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
public class SysUnqidAddDto implements Serializable {

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

