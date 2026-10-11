package com.yz.mall.tw.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * 坐标校验单测（节点 1，无 Spring 容器）
 */
class TwGpsLatestServiceImplTest {

    @Test
    void validCoordinate_ok() {
        Assertions.assertTrue(TwGpsLatestServiceImpl.isValidCoordinate(new BigDecimal("116.397128"), new BigDecimal("39.916527")));
        Assertions.assertTrue(TwGpsLatestServiceImpl.isValidCoordinate(new BigDecimal("-180"), new BigDecimal("-90")));
        Assertions.assertTrue(TwGpsLatestServiceImpl.isValidCoordinate(new BigDecimal("180"), new BigDecimal("90")));
    }

    @Test
    void validCoordinate_reject() {
        Assertions.assertFalse(TwGpsLatestServiceImpl.isValidCoordinate(null, new BigDecimal("39")));
        Assertions.assertFalse(TwGpsLatestServiceImpl.isValidCoordinate(new BigDecimal("116"), null));
        Assertions.assertFalse(TwGpsLatestServiceImpl.isValidCoordinate(new BigDecimal("181"), new BigDecimal("39")));
        Assertions.assertFalse(TwGpsLatestServiceImpl.isValidCoordinate(new BigDecimal("116"), new BigDecimal("91")));
    }
}
