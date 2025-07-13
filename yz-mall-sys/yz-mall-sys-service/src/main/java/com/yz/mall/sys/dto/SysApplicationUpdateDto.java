package com.yz.mall.sys.dto;

import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 应用配置(SysApplication)表更新数据模型类
 *
 * @author yunze
 * @since 2024-08-11 20:10:15
 */
@Data
public class SysApplicationUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotBlank(message = "主键标识不能为空")
    private String id;

    /**
     * 应用id
     */
    private String clientId;

    /**
     * 应用密钥
     */
    private String clientSecret;

    /**
     * 应用名称
     */
    private String clientName;

    /**
     * 备注说明信息
     */
    private String remark;


}

