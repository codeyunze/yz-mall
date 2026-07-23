package com.yz.mall.oms.vo;

import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单商品详情信息
 *
 * @author yunze
 * @since 2025-01-30 19:14:03
 */
@Data
public class OmsOrderProductVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识 {@link OmsOrderRelationProduct#getId()}
     */
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 下单SKU Id
     */
    private Long skuId;

    /**
     * SKU编码快照
     */
    private String skuCode;

    /**
     * SKU名称快照
     */
    private String skuName;

    /**
     * 购买商品数量
     */
    private Integer productQuantity;

    /**
     * 已退款数量
     */
    private Integer refundQuantity;

    /**
     * 商品优惠金额（分）
     */
    private Long discountAmount;

    /**
     * 商品优惠后的实际单价（分）
     */
    private Long realAmount;

    /**
     * 商品属性;[{key:颜色,value:黑色},{key:内存,value:32G}]
     */
    private String productAttributes;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格(下单时单价，分)
     */
    private Long productPrice;

    /**
     * 商品备注信息
     */
    private String remark;

    /**
     * 商品图片id，限制为5张，以逗号分割
     */
    private String albumPics;

    /**
     * 商品预览地址
     */
    private String previewAddress;
}

