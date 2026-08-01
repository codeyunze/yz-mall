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
 * SaaS-租户主表(SaasTenant)更新数据模型类
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
@Data
public class SaasTenantUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     * <p>
     * 租户的唯一ID，更新操作时必填。
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 租户编码
     * <p>
     * 租户的唯一业务编码，用于系统内部标识租户，长度不超过64个字符。
     */
    @Length(max = 64, message = "租户编码不能超过64个字符")
    @NotBlank(message = "租户编码不能为空")
    private String tenantCode;

    /**
     * 租户名称
     * <p>
     * 租户的显示名称，长度不超过128个字符。
     */
    @Length(max = 128, message = "租户名称不能超过128个字符")
    @NotBlank(message = "租户名称不能为空")
    private String tenantName;

    /**
     * 联系人姓名
     * <p>
     * 租户负责人的姓名，长度不超过64个字符。
     */
    @Length(max = 64, message = "联系人不能超过64个字符")
    private String contactName;

    /**
     * 联系电话
     * <p>
     * 租户负责人的联系方式，长度不超过32个字符。
     */
    @Length(max = 32, message = "联系电话不能超过32个字符")
    private String contactPhone;

    /**
     * 过期时间
     * <p>
     * 租户服务的有效截止日期，为空表示永久有效或无限制。
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
     * 备注信息
     * <p>
     * 关于租户的其他补充说明，长度不超过500个字符。
     */
    @Length(max = 500, message = "备注不能超过500个字符")
    private String remark;
}