package com.yz.mall.sys.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 系统-用户收货信息(SysReceiptInfo)表更新数据模型类
 *
 * @author yunze
 * @since 2025-03-03 15:40:28
 */
@Data
public class SysReceiptInfoUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键标识
     */
    @NotNull(message = "主键标识不能为空")
    private Long id;

    /**
     * 收货地址省编码
     */
    private String receiverProvince;

    /**
     * 收货地址市编码
     */
    private String receiverCity;

    /**
     * 收货地址区编码
     */
    private String receiverDistrict;

    /**
     * 详细地址
     */
    private String receiverAddress;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机号
     */
    private String receiverPhone;

    /**
     * 收货人邮件
     */
    private String receiverEmail;

    /**
     * 创建人Id
     */
    private Long createId;


}

