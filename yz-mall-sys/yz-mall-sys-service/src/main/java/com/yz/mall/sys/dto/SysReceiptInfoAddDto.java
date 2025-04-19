package com.yz.mall.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统-用户收货信息(SysReceiptInfo)表新增数据模型类
 *
 * @author yunze
 * @since 2025-03-03 15:40:27
 */
@Data
public class SysReceiptInfoAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 收货地址省编码
     */
    @NotBlank(message = "收货地址省编码不能为空")
    private String receiverProvince;

    /**
     * 收货地址市编码
     */
    @NotBlank(message = "收货地址市编码不能为空")
    private String receiverCity;

    /**
     * 收货地址区编码
     */
    @NotBlank(message = "收货地址区编码不能为空")
    private String receiverDistrict;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String receiverAddress;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /**
     * 收货人手机号
     */
    @NotBlank(message = "收货人手机号不能为空")
    private String receiverPhone;

    /**
     * 收货人邮件
     */
    private String receiverEmail;

    /**
     * 创建人
     */
    private Long createId;
}

