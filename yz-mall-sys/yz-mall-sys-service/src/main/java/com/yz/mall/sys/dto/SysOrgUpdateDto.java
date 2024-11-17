package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 系统-组织表(SysOrg)表更新数据模型类
 *
 * @author yunze
 * @since 2024-11-17 20:19:07
 */
@Data
public class SysOrgUpdateDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织所属用户
     */
    private Long userId;


}

