package com.yz.mall.sys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


/**
 * 系统字典表(SysDictionary)表新增数据模型类
 *
 * @author yunze
 * @since 2025-11-30 09:53:52
 */
@Data
public class SysDictionaryAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 祖先ID，如果没有父节点则为0
     */
    private Long ancestorId;

    /**
     * 父节点ID，如果没有父节点则为0
     */
    private Long parentId;

    /**
     * 键
     */
    @NotBlank(message = "键不能为空")
    private String dictionaryKey;

    /**
     * 值
     */
    @NotBlank(message = "值不能为空")
    private String dictionaryValue;

    /**
     * 排序字段，数值越小排序越靠前
     */
    private Integer sortOrder;


}

