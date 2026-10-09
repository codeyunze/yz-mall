package com.yz.mall.tw.access.controller;

import com.yz.mall.tw.access.dto.EmqxAclRequest;
import com.yz.mall.tw.access.dto.EmqxAuthRequest;
import com.yz.mall.tw.access.dto.EmqxAuthResponse;
import com.yz.mall.tw.access.dto.EmqxGpsBridgeRequest;
import com.yz.mall.tw.access.dto.EmqxWebhookEvent;
import com.yz.mall.tw.access.service.EmqxAuthService;
import com.yz.mall.tw.access.service.OnlineStatusService;
import com.yz.mall.tw.access.service.TelemetryBridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * EMQX Cloud / 自建 EMQX 回调入口（鉴权、ACL、Webhook、GPS 桥接）。
 * <p>
 * 须在 Sa-Token 中放行 {@code /emqx/**}。
 */
@RestController
@RequestMapping("emqx")
@RequiredArgsConstructor
public class EmqxCallbackController {

    private final EmqxAuthService authService;
    private final OnlineStatusService onlineStatusService;
    private final TelemetryBridgeService telemetryBridgeService;

    /**
     * EMQX 5 HTTP Authenticator
     */
    @PostMapping("auth")
    public EmqxAuthResponse auth(@RequestBody EmqxAuthRequest request) {
        return authService.authenticate(request);
    }

    /**
     * EMQX 5 HTTP Authorization
     */
    @PostMapping("acl")
    public EmqxAuthResponse acl(@RequestBody EmqxAclRequest request) {
        return authService.authorize(request);
    }

    /**
     * 连接/断开 Webhook
     */
    @PostMapping("webhook")
    public Map<String, Object> webhook(@RequestBody EmqxWebhookEvent event) {
        onlineStatusService.handleWebhook(event);
        Map<String, Object> ok = new HashMap<>(1);
        ok.put("result", "ok");
        return ok;
    }

    /**
     * Rule Engine HTTP 动作：上行 GPS → Kafka
     */
    @PostMapping("bridge/gps")
    public Map<String, Object> bridgeGps(@RequestBody EmqxGpsBridgeRequest request) {
        boolean sent = telemetryBridgeService.bridgeGps(request);
        Map<String, Object> ok = new HashMap<>(2);
        ok.put("result", sent ? "ok" : "skip");
        ok.put("sent", sent);
        return ok;
    }
}
