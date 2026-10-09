package com.yz.mall.tw.access.service;

import com.yz.mall.tw.access.config.TwAccessProperties;
import com.yz.mall.tw.access.constant.TwAccessConstants;
import com.yz.mall.tw.access.dto.EmqxWebhookEvent;
import com.yz.mall.tw.access.client.MallTwDeviceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 上下线 → Redis {@code tw:online:{vin}}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineStatusService {

    private final StringRedisTemplate stringRedisTemplate;
    private final TwAccessProperties properties;
    private final DeviceSessionCache sessionCache;
    private final MallTwDeviceClient deviceClient;

    /**
     * 处理 EMQX 连接/断开 Webhook。
     */
    public void handleWebhook(EmqxWebhookEvent event) {
        if (event == null || event.getEvent() == null) {
            return;
        }
        String vin = resolveVin(event);
        if (vin == null || vin.isBlank()) {
            log.warn("在线状态忽略：无法解析 VIN event={} clientid={}", event.getEvent(), event.getClientid());
            return;
        }
        String key = properties.getOnline().getRedisPrefix() + vin;
        if (TwAccessConstants.EVENT_CONNECTED.equals(event.getEvent())) {
            long ttl = properties.getOnline().getTtlSeconds();
            if (ttl > 0) {
                stringRedisTemplate.opsForValue().set(key, properties.getOnline().getOnlineValue(), ttl, TimeUnit.SECONDS);
            } else {
                stringRedisTemplate.opsForValue().set(key, properties.getOnline().getOnlineValue());
            }
            log.info("车辆上线 vin={} clientid={}", vin, event.getClientid());
            return;
        }
        if (TwAccessConstants.EVENT_DISCONNECTED.equals(event.getEvent())) {
            stringRedisTemplate.delete(key);
            if (event.getClientid() != null) {
                sessionCache.remove(event.getClientid());
            }
            log.info("车辆下线 vin={} clientid={} reason={}", vin, event.getClientid(), event.getReason());
        }
    }

    private String resolveVin(EmqxWebhookEvent event) {
        String vin = sessionCache.getVin(event.getClientid());
        if (vin != null) {
            return vin;
        }
        vin = sessionCache.getVin(event.getUsername());
        if (vin != null) {
            return vin;
        }
        String deviceId = event.getUsername() != null ? event.getUsername() : event.getClientid();
        if (deviceId == null) {
            return null;
        }
        return deviceClient.getBoundVin(deviceId);
    }
}
