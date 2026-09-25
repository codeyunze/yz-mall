package com.yz.mall.tw.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 轨迹查询出参
 */
@Data
public class TwGpsTrackVo {

    /**
     * 车架号
     */
    private String vin;
    /**
     * 折线点（已按时间升序，可能已抽稀）
     */
    private List<TwGpsTrackPointVo> points = new ArrayList<>();
    /**
     * 抽稀前命中点数
     */
    private int total;
    /**
     * 是否已抽稀
     */
    private boolean sampled;
}
