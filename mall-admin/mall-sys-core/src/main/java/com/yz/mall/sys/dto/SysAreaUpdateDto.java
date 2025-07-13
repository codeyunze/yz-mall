package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * (SysArea)表更新数据模型类
 *
 * @author yunze
 * @since 2025-03-03 22:38:56
 */
@Data
public class SysAreaUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    private String name;

    /**
     * 父类Code
     */
    private String parentId;

    /**
     * 等级
     */
    private Integer levelType;

    private String parentPath;

    private String parentName;

    /**
     * 排序
     */
    private Integer sortNum;

    /**
     * 状态：0禁用，1正常
     */
    private Integer status;


}

