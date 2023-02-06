package com.yz.openinterface.business.shop.stock.entity;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 库存表(ShopStock)表实体类
 *
 * @author makejava
 * @since 2023-02-06 22:44:54
 */
@SuppressWarnings("serial")
public class ShopStock extends Model<ShopStock> {
    // 自增主键
    private Long id;
    // 商品编号
    private String productNumber;
    // 商品库存
    private Integer productStock;
    // 创建时间
    private Date createDate;
    // 更新时间
    private Date updateDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

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

