package com.yz.mall.tw.device.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 鉴权用终端凭证摘要（供 tw-access）
 */
@Data
public class ExtendTwDeviceAuthVo implements Serializable {
    private String deviceId;
    private Boolean enabled;
    private Boolean bound;
    private String vin;
    private Long vehicleId;
    private String secretHash;
    private String secretAlgo;
    private String secretSalt;
}
