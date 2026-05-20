package com.yz.mall.sys.dto;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SaaS-租户主表(SaasTenant)新增数据模型类
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
@Data
public class SaasTenantAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编码，全局唯一
     */
    @Length(max = 64, message = "租户编码不能超过64个字符")
    @NotBlank(message = "租户编码不能为空")
    private String tenantCode;

    /**
     * 租户名称
     */
    @Length(max = 128, message = "租户名称不能超过128个字符")
    @NotBlank(message = "租户名称不能为空")
    private String tenantName;

    /**
     * 联系人
     */
    @Length(max = 64, message = "联系人不能超过64个字符")
    private String contactName;

    /**
     * 联系电话
     */
    @Length(max = 32, message = "联系电话不能超过32个字符")
    private String contactPhone;

    /**
     * 租户到期时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime expireTime;

    /**
     * 租户状态：0停用，1启用
     */
    @NotNull(message = "租户状态不能为空")
    private Integer tenantStatus;

    /**
     * 备注
     */
    @Length(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
