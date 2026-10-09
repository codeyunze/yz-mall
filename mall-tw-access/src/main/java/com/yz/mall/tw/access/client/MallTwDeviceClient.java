package com.yz.mall.tw.access.client;

import com.yz.mall.base.Result;
import com.yz.mall.tw.access.config.TwAccessProperties;
import com.yz.mall.tw.access.feign.ExtendTwDeviceFeign;
import com.yz.mall.tw.access.feign.TwDeviceVerifyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 调用 mall-tw 终端 Extend（OpenFeign）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MallTwDeviceClient {

    private final ExtendTwDeviceFeign deviceFeign;
    private final TwAccessProperties properties;

    /**
     * 校验设备密钥且已绑定车辆。
     *
     * @param deviceId MQTT username 或 clientId（设备号）
     * @param password MQTT password
     * @return 绑定 VIN；失败返回 null
     */
    public String verifyAndGetVin(String deviceId, String password) {
        if (properties.getAuth().isDevBypass()) {
            return resolveDevVin(deviceId);
        }
        try {
            TwDeviceVerifyRequest req = new TwDeviceVerifyRequest();
            req.setDeviceId(deviceId);
            req.setPassword(password);
            Result<Boolean> result = deviceFeign.verify(req);
            if (result == null || result.getCode() != 200 || !Boolean.TRUE.equals(result.getData())) {
                return null;
            }
            return getBoundVin(deviceId);
        } catch (Exception ex) {
            log.warn("调用 mall-tw verify 失败 deviceId={}: {}", deviceId, ex.getMessage());
            return null;
        }
    }

    /**
     * 查询绑定 VIN。
     */
    public String getBoundVin(String deviceId) {
        if (properties.getAuth().isDevBypass()) {
            return resolveDevVin(deviceId);
        }
        try {
            Result<String> result = deviceFeign.boundVin(deviceId);
            if (result == null || result.getCode() != 200 || result.getData() == null || result.getData().isBlank()) {
                return null;
            }
            return result.getData().trim().toUpperCase();
        } catch (Exception ex) {
            log.warn("调用 mall-tw bound-vin 失败 deviceId={}: {}", deviceId, ex.getMessage());
            return null;
        }
    }

    private String resolveDevVin(String deviceId) {
        if (deviceId != null && !deviceId.isBlank() && deviceId.trim().length() >= 8) {
            return deviceId.trim().toUpperCase();
        }
        return properties.getAuth().getDevDefaultVin().trim().toUpperCase();
    }
}
