package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 应用配置(SysApplication)表新增数据模型类
 *
 * @author yunze
 * @since 2024-08-11 20:10:14
 */
@Data
public class SysApplicationAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 应用id
     */
    @NotBlank(message = "应用id不能为空")
    private String clientId;

    /**
     * 应用密钥
     */
    @NotBlank(message = "应用密钥不能为空")
    private String clientSecret;

    /**
     * 应用名称
     */
    @NotBlank(message = "应用名称不能为空")
    private String clientName;

    /**
     * 备注说明信息
     */
    @NotBlank(message = "备注说明信息不能为空")
    private String remark;


}

