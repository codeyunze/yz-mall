package com.yz.mall.pms.vo;

import com.yz.mall.pms.entity.PmsProduct;
import com.yz.mall.pms.entity.PmsStock;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品展示信息
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Data
public class PmsProductDisplayInfoVo implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识 {@link PmsProduct#getId()}
     */
    private Long id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格
     */
    private BigDecimal productPrice;

    /**
     * 商品标签
     */
    private String titles;

    /**
     * 商品备注信息
     */
    private String remark;

    /**
     * 商品图片id，限制为5张，以逗号分割
     */
    private String albumPics;

    /**
     * 商品图片预览地址
     */
    private List<String> productImages;

    /**
     * 库存数量 {@link PmsStock#getQuantity()}
     */
    private Integer quantity = 0;

    /**
     * 上架状态: 0：下架，1：上架 <br/>
     * 对应数据表字段 {@link PmsProduct#getPublishStatus()} <br/>
     * 对应枚举信息 {@link com.yz.mall.pms.enums.ProductPublishStatusEnum}
     */
    private Integer publishStatus;
}

