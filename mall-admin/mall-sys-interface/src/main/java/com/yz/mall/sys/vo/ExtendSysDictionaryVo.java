package com.yz.mall.sys.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据字典-树形结构
 *
 * @author yunze
 * @since 2025-11-30 09:53:52
 */
@Data
public class ExtendSysDictionaryVo {

    /**
     * 主键标识
     */
    private Long id;

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

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 子节点
     */
    private List<ExtendSysDictionaryVo> children;

}

