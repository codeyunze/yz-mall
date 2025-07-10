package com.yz.mall.sys.vo;

import lombok.Data;

/**
 * 系统-用户收货信息
 *
 * @author yunze
 * @since 2025-03-03 15:40:27
 */
@Data
public class SysReceiptInfoVo {

    /**
     * 主键标识
     */
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
     * 收货地址省名称
     */
    private String receiverProvinceName;

    /**
     * 收货地址市名称
     */
    private String receiverCityName;

    /**
     * 收货地址区名称
     */
    private String receiverDistrictName;

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

}

