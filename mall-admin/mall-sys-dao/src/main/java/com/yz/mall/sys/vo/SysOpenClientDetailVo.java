package com.yz.mall.sys.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yz.mall.sys.entity.SysOpenClientAuth;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 第三方开放客户端详情（含当前公钥摘要与授权列表）
 *
 * @author yunze
 */
@Data
public class SysOpenClientDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 客户端标识
     */
    private String clientId;

    /**
     * 应用名称
     */
    private String clientName;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;

    /**
     * 授权到期时间，空表示长期
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime expireTime;

    /**
     * IP白名单
     */
    private String ipWhitelist;

    /**
     * QPS上限
     */
    private Integer rateLimitQps;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 当前生效公钥摘要；无公钥时为 null
     */
    private SysOpenClientKeySummaryVo currentKey;

    /**
     * 有效授权列表
     */
    private List<SysOpenClientAuth> authList;
}
