package com.yz.mall.sys.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 系统字典表(SysDictionary)表更新数据模型类
 *
 * @author yunze
 * @since 2025-11-30 09:53:53
 */
@Data
public class SysDictionaryUpdateDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 父节点ID，如果没有父节点则为0
     */
    private Long parentId;

    /**
     * 键
     */
    private String dictionaryKey;

    /**
     * 值
     */
    private String dictionaryValue;

    /**
     * 排序字段，数值越小排序越靠前
     */
    private Integer sortOrder;

    /**
     * 字典状态：0启用、1禁用
     */
    private Integer dictionaryEnable;
}

