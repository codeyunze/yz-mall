package com.yz.mall.sys.dto;

import java.time.LocalDateTime;

import lombok.Data;

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

    private static final long serialVesionUID = 1L;

    /**
     * 组织名称
     */
    @NotBlank(message = "组织名称不能为空")
    private String orgName;

    /**
     * 组织所属用户
     */
    @NotNull(message = "组织所属用户不能为空")
    private Long userId;


}

