package com.yz.mall.serial.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yunze
 * @date 2024/6/24 星期一 23:18
 */
@Data
public class ExtendUnqidDto implements Serializable {

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

    public ExtendUnqidDto() {
    }

    public ExtendUnqidDto(String prefix, Integer numberLength) {
        this.prefix = prefix;
        this.numberLength = numberLength;
    }

    public ExtendUnqidDto(String prefix, Integer numberLength, Integer quantity) {
        this.prefix = prefix;
        this.numberLength = numberLength;
        this.quantity = quantity;
    }
}
