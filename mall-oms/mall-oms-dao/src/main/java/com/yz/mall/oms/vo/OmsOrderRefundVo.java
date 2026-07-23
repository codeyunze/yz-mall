package com.yz.mall.oms.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 退款单列表/详情展示
 *
 * @author yunze
 * @since 2026-07-19
 */
@Data
public class OmsOrderRefundVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String refundNo;

    private Long orderId;

    private String orderCode;

    private Long userId;

    private Long businessOrgId;

    private Long refundAmount;

    private Integer reasonType;

    private String reason;

    private Integer refundStatus;

    private Long auditUserId;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime auditTime;

    private String auditRemark;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
}
