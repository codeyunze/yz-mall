package com.yz.mall.oms.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yz.mall.oms.entity.OmsOrder;
import lombok.Data;

/**
 * 订单基础信息
 *
 * @author yunze
 * @since 2025-01-30 19:12:58
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OmsOrderSlimVo extends Model<OmsOrderSlimVo> {

    /**
     * 订单主键标识 {@link OmsOrder#getId()}
     */
    private Long id;

    /**
     * 订单编号;省市区年月日000001
     */
    private String orderCode;

    public OmsOrderSlimVo() {
    }

    public OmsOrderSlimVo(Long id, String orderCode) {
        this.id = id;
        this.orderCode = orderCode;
    }
}

