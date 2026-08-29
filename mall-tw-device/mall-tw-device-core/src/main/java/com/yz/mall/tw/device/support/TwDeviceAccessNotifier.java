package com.yz.mall.tw.device.support;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.yz.mall.tw.device.constant.TwDeviceConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 接入侧副作用：清凭证缓存；踢连接待 tw-access 就绪后对接
 */
@Slf4j
@Component
public class TwDeviceAccessNotifier {

    private final StringRedisTemplate stringRedisTemplate;

    public TwDeviceAccessNotifier(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 绑定/解绑/禁用/重置后失效鉴权缓存，并预留踢连接钩子。
     *
     * @param deviceId 终端号
     * @param reason   原因
     */
    public void invalidateAndKick(String deviceId, String reason) {
        if (StrUtil.isBlank(deviceId)) {
            return;
        }
        stringRedisTemplate.delete(TwDeviceConstants.REDIS_CRED_CACHE_PREFIX + deviceId);
        // P0：tw-access 未就绪，仅打日志；后续改为 Feign/MQ 通知踢 EMQX 连接
        log.info("tw-device 通知接入侧失效凭证/踢连接, deviceId={}, reason={}", deviceId, reason);
    }

    /**
     * 生成随机 MQTT 明文密码（24 位字母数字）
     *
     * @return 明文密码
     */
    public static String randomSecret() {
        return RandomUtil.randomString(24);
    }

    /**
     * 生成 deviceId：TW + 雪花后 16 位
     *
     * @param snowflakeId 雪花ID
     * @return deviceId
     */
    public static String generateDeviceId(long snowflakeId) {
        String suffix = Long.toString(Math.abs(snowflakeId), 36).toUpperCase();
        if (suffix.length() > 16) {
            suffix = suffix.substring(suffix.length() - 16);
        }
        return TwDeviceConstants.DEVICE_ID_PREFIX + suffix;
    }
}
