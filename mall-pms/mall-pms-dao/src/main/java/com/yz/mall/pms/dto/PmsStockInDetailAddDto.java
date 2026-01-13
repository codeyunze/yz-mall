package com.yz.mall.pms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表新增数据模型类
 *
 * @author yunze
 * @since 2024-12-25 19:53:27
 */
@Data
public class PmsStockInDetailAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联商品Id
     */
    private Long productId;

    /**
     * 关联SKU Id
     */
    @NotNull(message = "关联SKU Id不能为空")
    private Long skuId;

    /**
     * 本次入库数量
     */
    private Integer quantity = 0;

    /**
     * 关联供应商Id
     */
    private Long supplierId;

    /**
     * 商品入库备注信息
     */
    @Length(max = 255, message = "商品入库备注说明不能超过255个字符")
    private String remark;

    public PmsStockInDetailAddDto(Long productId, Long skuId, Integer quantity, String remark) {
        this.productId = productId;
        this.skuId = skuId;
        this.quantity = quantity;
        this.remark = remark;
    }
}

