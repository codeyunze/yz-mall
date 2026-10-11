package com.yz.mall.tw.access.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进程内 clientId/deviceId → VIN 缓存（鉴权成功后供 ACL / Webhook）
 */
@Component
public class DeviceSessionCache {

    private final Map<String, String> map = new ConcurrentHashMap<>();

    public void put(String key, String vin) {
        if (key == null || key.isBlank() || vin == null || vin.isBlank()) {
            return;
        }
        map.put(key.trim(), vin.trim().toUpperCase());
    }

    public String getVin(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return map.get(key.trim());
    }

    public void remove(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        map.remove(key.trim());
    }
}
