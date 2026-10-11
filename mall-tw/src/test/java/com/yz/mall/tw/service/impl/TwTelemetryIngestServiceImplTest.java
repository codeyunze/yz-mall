package com.yz.mall.tw.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * GPS 时间解析单测
 */
class TwTelemetryIngestServiceImplTest {

    @Test
    @DisplayName("解析 ISO-8601 带偏移")
    void parseOffsetDateTime() {
        LocalDateTime t = TwTelemetryIngestServiceImpl.parseGpsTime("2026-08-04T15:00:01.000+08:00");
        Assertions.assertNotNull(t);
        Assertions.assertEquals(LocalDateTime.of(2026, 8, 4, 15, 0, 1), t);
    }

    @Test
    @DisplayName("解析常规日期时间")
    void parseNormDateTime() {
        LocalDateTime t = TwTelemetryIngestServiceImpl.parseGpsTime("2026-09-25 10:00:00");
        Assertions.assertEquals(LocalDateTime.of(2026, 9, 25, 10, 0, 0), t);
    }

    @Test
    @DisplayName("空串返回 null")
    void parseBlank() {
        Assertions.assertNull(TwTelemetryIngestServiceImpl.parseGpsTime(""));
        Assertions.assertNull(TwTelemetryIngestServiceImpl.parseGpsTime(null));
    }
}
