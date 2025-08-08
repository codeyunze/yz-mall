package com.yz.mall.pms.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表实体类
 *
 * @author yunze
 * @since 2024-12-25 19:53:26
 */
@Data
public class PmsStockInDetail extends Model<PmsStockInDetail> {

    /**
     * 主键标识
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
     * 数据是否有效：0数据有效
     */
    @TableLogic(value = "0", delval = "current_timestamp")
    private Long invalid;

    /**
     * 关联商品Id
     */
    private Long productId;

    /**
     * 入库编号
     */
    private String stockInCode;

    /**
     * 本次入库数量
     */
    private Integer quantity;

    /**
     * 关联供应商Id
     */
    private Long supplierId;

    /**
     * 商品入库备注信息
     */
    private String remark;

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

