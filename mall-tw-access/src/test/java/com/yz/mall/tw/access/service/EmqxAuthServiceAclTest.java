package com.yz.mall.tw.access.service;

import com.yz.mall.tw.access.config.TwAccessProperties;
import com.yz.mall.tw.access.dto.EmqxAclRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ACL Topic 前缀校验（轻量）
 */
class EmqxAuthServiceAclTest {

    @Test
    @DisplayName("本车 Topic 应允许")
    void allowOwnTopic() {
        TwAccessProperties props = new TwAccessProperties();
        props.setTopicPrefix("tsp");
        DeviceSessionCache cache = new DeviceSessionCache();
        cache.put("c1", "TESTVIN001");
        EmqxAuthService service = new EmqxAuthService(null, props, cache);
        EmqxAclRequest req = new EmqxAclRequest();
        req.setClientid("c1");
        req.setTopic("tsp/TESTVIN001/up/gps");
        req.setAction("publish");
        Assertions.assertEquals("allow", service.authorize(req).getResult());
    }

    @Test
    @DisplayName("跨车 Topic 应拒绝")
    void denyOtherVin() {
        TwAccessProperties props = new TwAccessProperties();
        props.setTopicPrefix("tsp");
        DeviceSessionCache cache = new DeviceSessionCache();
        cache.put("c1", "TESTVIN001");
        EmqxAuthService service = new EmqxAuthService(null, props, cache);
        EmqxAclRequest req = new EmqxAclRequest();
        req.setClientid("c1");
        req.setTopic("tsp/OTHERVIN/up/gps");
        Assertions.assertEquals("deny", service.authorize(req).getResult());
    }
}
