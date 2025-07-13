package com.yz.mall.sys.dto;

import com.yz.mall.sys.enums.EnableEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统-组织表(SysOrg)表新增数据模型类
 *
 * @author yunze
 * @since 2024-11-17 20:19:07
 */
@Data
public class SysOrgAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组织名称
     */
    @Length(max = 100, message = "组织名称不能超过100个字符")
    @NotBlank(message = "组织名称不能为空")
    private String orgName;

    /**
     * 组织所属用户
     */
    @NotNull(message = "组织所属用户不能为空")
    private Long userId;

    /**
     * 上级组织
     */
    private Long parentId = -1L;

    /**
     * 备注说明
     */
    @Length(max = 100, message = "备注说明不能超过100个字符")
    private String remark;

    /**
     * 状态1-启用,0-停用
     */
    private Integer status = EnableEnum.ENABLE.get();

    /**
     * 排序
     */
    private Integer sort = 99;

    /**
     * 组织路径
     */
    @Length(max = 100, message = "组织路径不能超过100个字符")
    private String orgPathId;
}

