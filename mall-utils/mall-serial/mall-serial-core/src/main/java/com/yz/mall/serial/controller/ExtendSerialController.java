package com.yz.mall.serial.controller;


import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.serial.dto.ExtendSerialDto;
import com.yz.mall.serial.service.ExtendSerialService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * 系统-流水号表(SysUnqid)表控制层
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@RestController
@RequestMapping("extend/serial/")
public class ExtendSerialController extends ApiController {

    private final ExtendSerialService service;

    public ExtendSerialController(ExtendSerialService service) {
        this.service = service;
    }


    /**
     * 生成流水号
     */
    @PostMapping("generateNumber")
    public Result<String> generateNumber(@RequestBody @Valid ExtendSerialDto dto) {
        // Thread.sleep(30000);
        return success(this.service.generateNumber(dto.getPrefix(), dto.getNumberLength()));
    }

}

