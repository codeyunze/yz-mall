package com.yz.mall.sys.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * (SysArea)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-03-03 22:38:56
 */
@Data
public class SysAreaQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父类Code
     */
    private String parentId;
}

