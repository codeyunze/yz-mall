package com.yz.mall.pms.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 产品管理-商品出库日志表(PmsStockOutDetail)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
@Data
public class PmsStockOutDetailQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 关联商品Id
     */
    private Long productId;

    /**
     * 出库编号
     */
    private String stockOutCode;

    /**
     * 本次出库数量
     */
    private Integer quantity;

    /**
     * 关联订单Id
     */
    private Long orderId;

    /**
     * 商品入库备注信息
     */
    private String remark;


}

