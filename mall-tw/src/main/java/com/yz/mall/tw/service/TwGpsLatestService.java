package com.yz.mall.tw.service;

import com.yz.mall.tw.dto.TwGpsLatestWriteDto;
import com.yz.mall.tw.vo.TwGpsLatestVo;

import java.util.Collection;
import java.util.List;

/**
 * 最新 GPS：Redis 热读 + MySQL 兜底
 */
public interface TwGpsLatestService {

    /**
     * 写入最新位置：先 Redis，再 MySQL UPSERT。
     *
     * @param dto 上报点
     * @return 写入后的最新位置；非法坐标返回 null（调用方打日志丢弃）
     */
    TwGpsLatestVo saveLatest(TwGpsLatestWriteDto dto);

    /**
     * 按 VIN 读最新位置：Redis → miss 则 MySQL → 回填 Redis。
     *
     * @param vin 车架号
     * @return 无数据返回 null
     */
    TwGpsLatestVo getLatestByVin(String vin);

    /**
     * 批量按 VIN 读取（优先 Redis MGET，缺失回源 MySQL）。
     *
     * @param vins VIN 列表
     * @return 有数据的列表（顺序不保证与入参一致）
     */
    List<TwGpsLatestVo> listLatestByVins(Collection<String> vins);
}
