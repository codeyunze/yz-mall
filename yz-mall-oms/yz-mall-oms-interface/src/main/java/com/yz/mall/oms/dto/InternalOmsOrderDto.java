package com.yz.mall.oms.dto;

import com.yz.mall.oms.enums.OmsOrderTypeEnum;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author yunze
 * @date 2025/1/30 19:25
 */
@Data
public class InternalOmsOrderDto implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 下单用户Id
     * @ignore
     */
    private Long userId;

    /**
     * 订单类型：0正常订单；1秒杀订单 {@link OmsOrderTypeEnum}
     */
    @Min(0)
    @Max(1)
    private Integer orderType;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机号
     */
    private String receiverPhone;

    /**
     * 收货省
     */
    private String receiverProvince;

    /**
     * 收货市
     */
    private String receiverCity;

    /**
     * 收货区
     */
    private String receiverRegion;

    /**
     * 收货详细地址
     */
    private String receiverAddress;

    /**
     * 订单消息接收邮箱
     */
    private String email;

    /**
     * 订单商品信息
     */
    @NotEmpty(message = "商品信息不能为空")
    private List<InternalOmsOrderProductDto> products;

    /**
     * 订单备注
     */
    private String note;
}
