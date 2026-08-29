package com.yz.mall.tw.device.service;

import com.yz.mall.tw.device.vo.ExtendTwDeviceAuthVo;
import com.yz.mall.tw.device.vo.ExtendTwDeviceSlimVo;

/**
 * 终端跨服务扩展接口
 */
public interface ExtendTwDeviceService {

    /**
     * 鉴权查询
     *
     * @param deviceId 终端号
     * @return 鉴权摘要，不存在返回 null
     */
    ExtendTwDeviceAuthVo getAuthByDeviceId(String deviceId);

    /**
     * 按 VIN 查当前有效绑定终端摘要
     *
     * @param vin 车架号
     * @return 摘要，可空
     */
    ExtendTwDeviceSlimVo getByVin(String vin);

    /**
     * 快速取绑定 VIN
     *
     * @param deviceId 终端号
     * @return VIN，未绑定返回 null
     */
    String getBoundVin(String deviceId);

    /**
     * 校验明文密码
     *
     * @param deviceId 终端号
     * @param password 明文密码
     * @return 是否匹配且可连接（启用且已绑定）
     */
    Boolean verify(String deviceId, String password);
}
