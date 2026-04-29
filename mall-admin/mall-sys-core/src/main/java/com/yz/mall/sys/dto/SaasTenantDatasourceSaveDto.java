package com.yz.mall.sys.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 租户数据源保存模型
 *
 * @author yunze
 * @since 2026-04-21 00:00:00
 */
@Data
public class SaasTenantDatasourceSaveDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @Length(max = 64, message = "服务标识不能超过64个字符")
    @NotBlank(message = "服务标识不能为空")
    private String serviceCode;

    @Length(max = 32, message = "数据库类型不能超过32个字符")
    @NotBlank(message = "数据库类型不能为空")
    private String dbType;

    @Length(max = 128, message = "数据库主机不能超过128个字符")
    @NotBlank(message = "数据库主机不能为空")
    private String dbHost;

    @NotNull(message = "数据库端口不能为空")
    private Integer dbPort;

    @Length(max = 128, message = "数据库名不能超过128个字符")
    @NotBlank(message = "数据库名不能为空")
    private String dbName;

    @Length(max = 128, message = "数据库用户名不能超过128个字符")
    @NotBlank(message = "数据库用户名不能为空")
    private String dbUsername;

    @Length(max = 512, message = "数据库密码密文不能超过512个字符")
    private String dbPasswordEnc;

    @Length(max = 512, message = "数据库连接参数不能超过512个字符")
    private String dbParams;

    private Integer dsStatus;

    @Length(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
