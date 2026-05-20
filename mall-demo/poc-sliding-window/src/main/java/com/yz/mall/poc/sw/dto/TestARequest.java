package com.yz.mall.poc.sw.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestARequest {

    private final List<String> vins;
    private final int maxVinCount;
    private final int timeWindow;

    @JsonCreator
    public TestARequest(
            @JsonProperty("vins") List<String> vins,
            @JsonProperty("maxVinCount") int maxVinCount,
            @JsonProperty("timeWindow") int timeWindow) {
        this.vins = vins == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(vins));
        this.maxVinCount = maxVinCount;
        this.timeWindow = timeWindow;
    }

    public List<String> getVins() {
        return vins;
    }

    public int getMaxVinCount() {
        return maxVinCount;
    }

    public int getTimeWindow() {
        return timeWindow;
    }
}
