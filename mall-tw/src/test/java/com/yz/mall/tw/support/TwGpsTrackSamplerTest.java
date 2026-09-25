package com.yz.mall.tw.support;

import com.yz.mall.tw.vo.TwGpsTrackPointVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 轨迹抽稀单测（节点 3）
 */
class TwGpsTrackSamplerTest {

    @Test
    @DisplayName("点数不超过上限时原样返回")
    void noSampleWhenWithinLimit() {
        List<TwGpsTrackPointVo> points = buildPoints(10);
        List<TwGpsTrackPointVo> result = TwGpsTrackSampler.sample(points, 20);
        Assertions.assertEquals(10, result.size());
        Assertions.assertSame(points, result);
    }

    @Test
    @DisplayName("超过上限等距抽稀并保留首尾")
    void sampleKeepsEnds() {
        List<TwGpsTrackPointVo> points = buildPoints(100);
        List<TwGpsTrackPointVo> result = TwGpsTrackSampler.sample(points, 10);
        Assertions.assertEquals(10, result.size());
        Assertions.assertEquals(points.get(0).getGpsTime(), result.get(0).getGpsTime());
        Assertions.assertEquals(points.get(99).getGpsTime(), result.get(9).getGpsTime());
    }

    private static List<TwGpsTrackPointVo> buildPoints(int n) {
        List<TwGpsTrackPointVo> list = new ArrayList<>(n);
        LocalDateTime base = LocalDateTime.of(2026, 9, 25, 10, 0, 0);
        for (int i = 0; i < n; i++) {
            TwGpsTrackPointVo vo = new TwGpsTrackPointVo();
            vo.setLng(new BigDecimal("116.0000000").add(new BigDecimal("0.0001").multiply(BigDecimal.valueOf(i))));
            vo.setLat(new BigDecimal("39.0000000"));
            vo.setGpsTime(base.plusSeconds(i));
            list.add(vo);
        }
        return list;
    }
}
