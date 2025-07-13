package com.yz.mall.sys.dto;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * (SysArea)表新增数据模型类
 *
 * @author yunze
 * @since 2025-03-03 22:38:56
 */
@Data
public class SysAreaAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Id不能为空")
    private String id;

    @NotBlank(message = "不能为空")
    private String name;

    /**
     * 父类Code
     */
    @NotBlank(message = "父类Code不能为空")
    private String parentId;

    /**
     * 等级
     */
    private Integer levelType;

    @NotBlank(message = "不能为空")
    private String parentPath;

    @NotBlank(message = "不能为空")
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

