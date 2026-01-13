package com.yz.mall.so.entity;

import java.time.LocalDateTime;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 分布式事务-订单表(SeataOrder)表实体类
 *
 * @author yunze
 * @since 2025-11-24 22:40:03
 */
@Data
public class SeataOrder extends Model<SeataOrder> {

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 订单状态:0-未支付,1-已支付
     */
    private Integer orderStatus;

    /**
     * 商品Id
     */
    private Long productId;

    /**
     * 商品金额，单位：分
     */
    private Long productPrice;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}

