package com.yz.mall.sys.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户端当前公钥摘要（不含完整公钥明文展示需求时的运维视图）
 *
 * @author yunze
 */
@Data
public class SysOpenClientKeySummaryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 密钥记录主键
     */
    private Long id;

    /**
     * 密钥版本号
     */
    private Integer keyVersion;

    /**
     * 公钥 SM3 指纹（便于核对）
     */
    private String fingerprint;

    /**
     * 公钥前缀预览（截断）
     */
    private String publicKeyPreview;

    /**
     * 生效时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime effectTime;

    /**
     * 备注
     */
    private String remark;
}
