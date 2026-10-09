package com.yz.mall.tw.access.controller;

import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.tw.access.dto.MqttPublishDto;
import com.yz.mall.tw.access.service.MqttPublishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接入对外业务接口（下行代理等）
 */
@RestController
@RequestMapping("tw/access")
@RequiredArgsConstructor
public class TwAccessController extends ApiController {

    private final MqttPublishService mqttPublishService;

    /**
     * 下行发布 MQTT（供 tw-command 调用）
     */
    // @SaCheckPermission("api:tw:access:publish")
    @PostMapping("mqtt/publish")
    public Result<Boolean> publish(@RequestBody @Valid MqttPublishDto dto) {
        mqttPublishService.publish(dto);
        return success(true);
    }
}
