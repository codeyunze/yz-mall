package com.yz.mall.tw.support;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.tw.constant.TwVehicleConstants;
import com.yz.mall.tw.vo.TwVehicleLocationVo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 只读 Redis：在线状态 / 最新 GPS（由接入/遥测域写入）
 */
@Component
public class TwVehicleRealtimeSupport {

    private static final ObjectMapper OBJECT_MAPPER = JacksonUtil.getObjectMapper();
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StringRedisTemplate stringRedisTemplate;

    public TwVehicleRealtimeSupport(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 批量读取在线标记；值非空且不为 0/false/offline 视为在线。
     *
     * @param vins VIN 列表
     * @return vin -> online
     */
    public Map<String, Boolean> batchOnline(Collection<String> vins) {
        if (vins == null || vins.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Boolean> result = new HashMap<>();
        for (String vin : vins) {
            if (StrUtil.isBlank(vin)) {
                continue;
            }
            String raw = stringRedisTemplate.opsForValue().get(TwVehicleConstants.REDIS_ONLINE_PREFIX + vin);
            result.put(vin, isOnlineValue(raw));
        }
        return result;
    }

    /**
     * 读取最新位置 JSON：{"lng":..,"lat":..,"speed":..,"reportTime":"yyyy-MM-dd HH:mm:ss"}
     *
     * @param vin 车架号
     * @return 位置，无数据返回 null
     */
    public TwVehicleLocationVo latestLocation(String vin) {
        if (StrUtil.isBlank(vin)) {
            return null;
        }
        String raw = stringRedisTemplate.opsForValue().get(TwVehicleConstants.REDIS_GPS_PREFIX + vin);
        if (StrUtil.isBlank(raw)) {
            return null;
        }
        try {
            JsonNode json = OBJECT_MAPPER.readTree(raw);
            TwVehicleLocationVo vo = new TwVehicleLocationVo();
            vo.setLng(toDecimal(json, "lng"));
            vo.setLat(toDecimal(json, "lat"));
            vo.setSpeed(toDecimal(json, "speed"));
            String reportTime = textOrNull(json, "reportTime");
            if (StrUtil.isBlank(reportTime)) {
                reportTime = textOrNull(json, "gpsTime");
            }
            if (StrUtil.isNotBlank(reportTime)) {
                vo.setReportTime(LocalDateTime.parse(reportTime, DT_FMT));
            }
            return vo;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 单车是否在线。
     *
     * @param vin 车架号
     * @return 是否在线
     */
    public boolean isOnline(String vin) {
        if (StrUtil.isBlank(vin)) {
            return false;
        }
        String raw = stringRedisTemplate.opsForValue().get(TwVehicleConstants.REDIS_ONLINE_PREFIX + vin);
        return isOnlineValue(raw);
    }

    private static String textOrNull(JsonNode json, String key) {
        JsonNode node = json.get(key);
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }

    private static BigDecimal toDecimal(JsonNode json, String key) {
        JsonNode node = json.get(key);
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isNumber()) {
            return node.decimalValue();
        }
        String text = node.asText();
        if (StrUtil.isBlank(text)) {
            return null;
        }
        return new BigDecimal(text);
    }

    private static boolean isOnlineValue(String raw) {
        if (StrUtil.isBlank(raw)) {
            return false;
        }
        String v = raw.trim().toLowerCase();
        return !(List.of("0", "false", "offline", "null").contains(v));
    }
}
