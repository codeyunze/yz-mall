package com.yz.mall.oms.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单信息详细数据模型类
 *
 * @author yunze
 * @since 2025-01-30 19:12:58
 */
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OmsOrderDetailVo extends OmsOrderVo {

    /**
     * 确认收货时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime receiveTime;

    /**
     * 支付方式：0未支付/待支付；1支付宝；2微信
     */
    private Integer payType;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime deliveryTime;

    /**
     * 收货状态：0未确认收货；1已确认收货
     */
    private Integer confirmStatus;

    /**
     * 订单备注
     */
    private String note;

    /**
     * 订单下的商品信息
     */
    private List<OmsOrderProductVo> products;
}

