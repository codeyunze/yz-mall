package com.yz.mall.poc.sw.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yunze
 * @since 2026/5/15 17:58
 */
@Data
public class ClientConfigVo {

    private String clientId;

    private List<String> vins;

    private List<String> vehicleModes;

    private List<String> vehicleSeries;

    private String factoryCode;

    private Integer maxVinCount;

    private Integer timeWindow;
}
