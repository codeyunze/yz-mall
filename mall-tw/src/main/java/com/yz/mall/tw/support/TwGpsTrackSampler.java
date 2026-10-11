package com.yz.mall.tw.support;

import com.yz.mall.tw.vo.TwGpsTrackPointVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 轨迹点等距抽稀
 */
public final class TwGpsTrackSampler {

    private TwGpsTrackSampler() {
    }

    /**
     * 均匀抽样至不超过 maxPoints；已不超过则原样返回。
     *
     * @param points    升序点列
     * @param maxPoints 上限（≥1）
     * @return 抽稀结果
     */
    public static List<TwGpsTrackPointVo> sample(List<TwGpsTrackPointVo> points, int maxPoints) {
        if (points == null || points.isEmpty()) {
            return Collections.emptyList();
        }
        int limit = Math.max(1, maxPoints);
        if (points.size() <= limit) {
            return points;
        }
        if (limit == 1) {
            return List.of(points.get(0));
        }
        List<TwGpsTrackPointVo> sampled = new ArrayList<>(limit);
        int lastIdx = points.size() - 1;
        for (int i = 0; i < limit; i++) {
            int idx = (int) ((long) i * lastIdx / (limit - 1));
            sampled.add(points.get(idx));
        }
        return sampled;
    }
}
