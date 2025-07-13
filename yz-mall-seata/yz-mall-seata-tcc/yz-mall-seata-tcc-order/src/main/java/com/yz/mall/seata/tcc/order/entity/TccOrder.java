package com.yz.mall.seata.tcc.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 订单信息(TOrder)表实体类
 *
 * @author yunze
 * @since 2023-11-05 19:59:16
 */
@Data
public class TccOrder extends Model<TccOrder> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**
     * 用户ID
     */
    private Long accountId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 订单状态，0：下单未支付；1：下单已支付；-1：下单失败
     */
    private Integer state;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 逻辑删除，0：有效数据；1：无效数据
     */
    @TableLogic
    private Integer invalid;
}

