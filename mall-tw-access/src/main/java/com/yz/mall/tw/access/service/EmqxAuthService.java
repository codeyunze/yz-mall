package com.yz.mall.tw.access.service;

import com.yz.mall.tw.access.config.TwAccessProperties;
import com.yz.mall.tw.access.dto.EmqxAclRequest;
import com.yz.mall.tw.access.dto.EmqxAuthRequest;
import com.yz.mall.tw.access.dto.EmqxAuthResponse;
import com.yz.mall.tw.access.client.MallTwDeviceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * EMQX CONNECT 鉴权与 Topic ACL
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmqxAuthService {

    private final MallTwDeviceClient deviceClient;
    private final TwAccessProperties properties;
    private final DeviceSessionCache sessionCache;

    /**
     * HTTP Authenticator：校验设备并缓存 clientId→VIN。
     */
    public EmqxAuthResponse authenticate(EmqxAuthRequest request) {
        if (request == null) {
            return EmqxAuthResponse.deny();
        }
        String deviceId = firstNonBlank(request.getUsername(), request.getClientid());
        String password = request.getPassword();
        if (isBlank(deviceId) || isBlank(password)) {
            log.warn("EMQX 鉴权拒绝：缺少 username/password");
            return EmqxAuthResponse.deny();
        }
        String vin = deviceClient.verifyAndGetVin(deviceId.trim(), password);
        if (isBlank(vin)) {
            log.warn("EMQX 鉴权拒绝：校验失败 deviceId={}", deviceId);
            return EmqxAuthResponse.deny();
        }
        String clientId = firstNonBlank(request.getClientid(), deviceId);
        sessionCache.put(clientId, vin);
        sessionCache.put(deviceId.trim(), vin);
        log.info("EMQX 鉴权通过 deviceId={} vin={}", deviceId, vin);
        return EmqxAuthResponse.allow();
    }

    /**
     * HTTP Authorization：仅允许本车 Topic 命名空间。
     */
    public EmqxAuthResponse authorize(EmqxAclRequest request) {
        if (request == null || isBlank(request.getTopic())) {
            return EmqxAuthResponse.deny();
        }
        String deviceId = firstNonBlank(request.getUsername(), request.getClientid());
        String vin = sessionCache.getVin(firstNonBlank(request.getClientid(), deviceId));
        if (isBlank(vin) && !isBlank(deviceId)) {
            vin = deviceClient.getBoundVin(deviceId.trim());
            if (!isBlank(vin) && !isBlank(request.getClientid())) {
                sessionCache.put(request.getClientid(), vin);
            }
        }
        if (isBlank(vin)) {
            log.warn("EMQX ACL 拒绝：无 VIN clientid={}", request.getClientid());
            return EmqxAuthResponse.deny();
        }
        String allowedPrefix = properties.getTopicPrefix() + "/" + vin + "/";
        String topic = request.getTopic();
        if (topic.startsWith(allowedPrefix) || topic.equals(properties.getTopicPrefix() + "/" + vin)) {
            return EmqxAuthResponse.allow();
        }
        log.warn("EMQX ACL 拒绝：topic={} 不在 {}*", topic, allowedPrefix);
        return EmqxAuthResponse.deny();
    }

    private static String firstNonBlank(String a, String b) {
        if (!isBlank(a)) {
            return a;
        }
        return b;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
