package com.yz.mall.serial.feign;

import com.yz.mall.base.Result;
import com.yz.mall.serial.dto.ExtendUnqidDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

/**
 * @author yunze
 * @date 2024/6/24 星期一 23:36
 */
@FeignClient(name = "mall-serial", value = "mall-serial", contextId = "extendSerial", path = "extend/serial/v3")
public interface ExtendSerialFeign {

    /**
     * 生成流水号
     */
    @PostMapping("/generateNumber")
    public Result<String> generateNumber(@RequestBody @Valid ExtendUnqidDto dto);
}
