package com.yz.mall.tw.dto;

import lombok.Data;

import java.util.List;

/**
 * 批量最新 GPS 查询
 */
@Data
public class TwGpsLatestBatchQueryDto {

    /**
     * VIN 列表；空则返回空列表（全量可见范围留作后续）
     */
    private List<String> vins;
}
