package com.yz.mall.sys.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 第三方开放客户端(SysOpenClient)表新增数据模型类
 *
 * @author yunze
 */
@Data
public class SysOpenClientAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用名称
     */
    @Length(max = 128, message = "应用名称不能超过128个字符")
    @NotBlank(message = "应用名称不能为空")
    private String clientName;

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
     * 授权到期时间，空表示长期
     */
    private LocalDateTime expireTime;

    /**
     * IP白名单，逗号分隔，空不限制
     */
    @Length(max = 512, message = "IP白名单不能超过512个字符")
    private String ipWhitelist;

    /**
     * 可选QPS上限
     */
    private Integer rateLimitQps;

    /**
     * 备注
     */
    @Length(max = 255, message = "备注不能超过255个字符")
    private String remark;
}
