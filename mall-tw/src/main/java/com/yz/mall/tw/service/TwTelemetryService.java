package com.yz.mall.tw.service;

import com.yz.mall.tw.dto.TwGpsLatestBatchQueryDto;
import com.yz.mall.tw.dto.TwGpsTrackQueryDto;
import com.yz.mall.tw.vo.TwGpsLatestVo;
import com.yz.mall.tw.vo.TwGpsTrackVo;

import java.util.List;

/**
 * 遥测查询门面：访问校验 + Latest / Track
 */
public interface TwTelemetryService {

    /**
     * 单车最新位置（vin / vehicleId 二选一）；含 access/check scope=2。
     *
     * @param vin       车架号
     * @param vehicleId 车辆 ID
     * @return 无位置数据时返回 null
     */
    TwGpsLatestVo getLatest(String vin, Long vehicleId);

    /**
     * 批量最新位置（运营/监控）；VIN 数 ≤ {@link com.yz.mall.tw.constant.TwTelemetryConstants#LATEST_BATCH_MAX}。
     *
     * @param dto 入参
     * @return 有数据的列表
     */
    List<TwGpsLatestVo> batchLatest(TwGpsLatestBatchQueryDto dto);

    /**
     * Extend：按 VIN 取最新位置摘要（不做登录用户数据范围校验，由调用方负责）。
     *
     * @param vin 车架号
     * @return 无数据返回 null
     */
    TwGpsLatestVo getLatestForExtend(String vin);

    /**
     * 轨迹查询（ClickHouse）；含 access/check scope=2、时间窗与抽稀。
     *
     * @param dto 查询条件
     * @return 折线结果；无数据时 points 为空
     */
    TwGpsTrackVo queryTrack(TwGpsTrackQueryDto dto);
}
