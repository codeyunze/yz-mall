package com.yz.mall.oms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 订单商品关联表(OmsOrderProductRelation)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-18 12:51:39
 */
@Data
public class OmsOrderProductRelationQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;


    /**
     * 主键标识
     */
    private String id;

    /**
     * 创建人
     */
    private String createdId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updatedId;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 数据是否有效：0数据有效
     */
    private Integer invalid;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 购买商品数量
     */
    private Integer productQuantity;

    /**
     * 商品优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 商品优惠后的实际价格
     */
    private BigDecimal realAmount;

    /**
     * 商品属性;[{key:颜色,value:黑色},{key:内存,value:32G}]
     */
    private String productAttributes;

}

