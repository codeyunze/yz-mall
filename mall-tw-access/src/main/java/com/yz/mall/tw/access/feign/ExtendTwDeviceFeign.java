package com.yz.mall.tw.access.feign;

import com.yz.mall.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * mall-tw 终端 Extend（Nacos 服务名 {@code mall-tw}；本地可配 url 直连）
 */
@FeignClient(name = "mall-tw", contextId = "extendTwDevice", path = "extend/tw/device", url = "${tw.access.mall-tw.url:}")
public interface ExtendTwDeviceFeign {

    /**
     * 校验设备明文密码
     */
    @PostMapping("verify")
    Result<Boolean> verify(@RequestBody TwDeviceVerifyRequest request);

    /**
     * 查询绑定 VIN
     */
    @GetMapping("{deviceId}/bound-vin")
    Result<String> boundVin(@PathVariable("deviceId") String deviceId);
}
