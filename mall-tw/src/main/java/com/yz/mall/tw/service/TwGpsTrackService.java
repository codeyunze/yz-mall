package com.yz.mall.tw.service;

import com.yz.mall.tw.dto.TwGpsTrackPointDto;
import com.yz.mall.tw.dto.TwGpsTrackQueryDto;
import com.yz.mall.tw.vo.TwGpsTrackVo;

import java.util.List;

/**
 * 轨迹写缓冲与查询
 */
public interface TwGpsTrackService {

    /**
     * 灌入轨迹点（入本地缓冲，异步批量写 CH）。
     *
     * @param points 点列表
     * @return 接受条数
     */
    int ingest(List<TwGpsTrackPointDto> points);

    /**
     * 强制刷盘缓冲到 ClickHouse。
     */
    void flush();

    /**
     * 查询轨迹（仅 ClickHouse）；含时间窗与抽稀。
     *
     * @param dto 查询条件
     * @return 折线结果
     */
    TwGpsTrackVo queryTrack(TwGpsTrackQueryDto dto);
}
