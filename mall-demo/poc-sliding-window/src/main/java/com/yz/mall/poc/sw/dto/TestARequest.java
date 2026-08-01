package com.yz.mall.poc.sw.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestARequest {

    private final List<String> userIds;
    private final int maxUserIdCount;
    private final int timeWindow;

    @JsonCreator
    public TestARequest(
            @JsonProperty("userIds") List<String> userIds,
            @JsonProperty("maxUserIdCount") int maxUserIdCount,
            @JsonProperty("timeWindow") int timeWindow) {
        this.userIds = userIds == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(userIds));
        this.maxUserIdCount = maxUserIdCount;
        this.timeWindow = timeWindow;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public int getMaxUserIdCount() {
        return maxUserIdCount;
    }

    public int getTimeWindow() {
        return timeWindow;
    }
}
