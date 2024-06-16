package com.yz.cases.mall.dto;

import java.time.LocalDateTime;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统-数据源信息(SysDatasource)表新增数据模型类
 *
 * @author yunze
 * @since 2024-06-12 11:02:09
 */
@Data
public class SysDatasourceAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 数据源访问用户名
     */
    @NotBlank(message = "数据源访问用户名不能为空")
    private String username;

    /**
     * 数据源访问密码
     */
    @NotBlank(message = "数据源访问密码不能为空")
    private String password;

    /**
     * 数据源驱动类
     */
    @NotBlank(message = "数据源驱动类不能为空")
    private String driverClassName;

    /**
     * 数据源访问地址
     */
    @NotBlank(message = "数据源访问地址不能为空")
    private String url;

}

