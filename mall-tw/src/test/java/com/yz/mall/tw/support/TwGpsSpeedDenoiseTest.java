package com.yz.mall.tw.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 速度跳变去噪单测
 */
class TwGpsSpeedDenoiseTest {

    @Test
    @DisplayName("正常车速不丢弃")
    void keepNormalSpeed() {
        LocalDateTime t0 = LocalDateTime.of(2026, 9, 25, 10, 0, 0);
        // 约 100m / 10s ≈ 36 km/h
        boolean drop = TwGpsSpeedDenoise.shouldDrop(
                new BigDecimal("116.397000"), new BigDecimal("39.916000"), t0,
                new BigDecimal("116.398000"), new BigDecimal("39.916000"), t0.plusSeconds(10),
                250D);
        Assertions.assertFalse(drop);
    }

    @Test
    @DisplayName("瞬移超速应丢弃")
    void dropJump() {
        LocalDateTime t0 = LocalDateTime.of(2026, 9, 25, 10, 0, 0);
        // 约 1 度经度 ≈ 85km / 1s 远超 250
        boolean drop = TwGpsSpeedDenoise.shouldDrop(
                new BigDecimal("116.0"), new BigDecimal("39.0"), t0,
                new BigDecimal("117.0"), new BigDecimal("39.0"), t0.plusSeconds(1),
                250D);
        Assertions.assertTrue(drop);
    }
}
