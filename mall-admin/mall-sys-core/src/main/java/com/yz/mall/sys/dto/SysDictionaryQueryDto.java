package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 系统字典表(SysDictionary)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-11-30 09:53:53
 */
@Data
public class SysDictionaryQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
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
     * 创建时间
     */
    private LocalDateTime createTime;


}

