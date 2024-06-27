package com.yz.unqid.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author yunze
 * @date 2024/6/24 星期一 23:18
 */
public class InternalUnqidDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流水号前缀
     */
    @NotBlank(message = "流水号前缀不能为空")
    private String prefix;

    /**
     * 流水号的序号长度（默认长度为6）
     * @mock 6
     */
    private Integer numberLength = 6;

    /**
     * 生成流水号数量（默认为1）
     * @mock 1
     */
    private Integer quantity = 1;

    public InternalUnqidDto() {
    }

    public InternalUnqidDto(String prefix, Integer numberLength) {
        this.prefix = prefix;
        this.numberLength = numberLength;
    }

    public InternalUnqidDto(String prefix, Integer numberLength, Integer quantity) {
        this.prefix = prefix;
        this.numberLength = numberLength;
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getNumberLength() {
        return numberLength;
    }

    public void setNumberLength(Integer numberLength) {
        this.numberLength = numberLength;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
