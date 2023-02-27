package com.yz.openinterface.business.shop.order.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单表(ShopOrder)表实体类
 *
 * @author makejava
 * @since 2023-02-06 23:45:11
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("serial")
public class ShopOrder extends Model<ShopOrder> {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 订单编号
     * @mock 0000012
     */
    private String orderNumber;

    /**
     * 商品编号
     * @mock SP001
     */
    private String productNumber;

    /**
     * 商品数量
     * @mock 7
     */
    private Integer productQuantity;

    /**
     * 创建时间
     * @mock 2023-01-01 01:01:01
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 更新时间
     *
     * @ignore 加上该注解表示接口文档不展示该字段
     */
    private LocalDateTime updateDate;

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

