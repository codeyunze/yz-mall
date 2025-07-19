package com.yz.mall.serial.controller;


import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.serial.dto.ExtendUnqidDto;
import com.yz.mall.serial.service.ExtendUnqidService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统-流水号表(SysUnqid)表控制层
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@RestController
@RequestMapping("extend/serial/")
public class ExtendSerialController extends ApiController {

    private final ExtendUnqidService service;

    public ExtendSerialController(ExtendUnqidService service) {
        this.service = service;
    }


    /**
     * 生成流水号
     */
    @PostMapping("generateNumber")
    public Result<String> generateNumber(@RequestBody @Valid ExtendUnqidDto dto) {
        // Thread.sleep(30000);
        return success(this.service.generateSerialNumber(dto.getPrefix(), dto.getNumberLength()));
    }

    /**
     * 批量生成流水号
     */
    @PostMapping("generateSerialNumbers")
    public Result<List<String>> generateSerialNumbers(@RequestBody @Valid ExtendUnqidDto dto) {
        return success(this.service.generateSerialNumbers(dto.getPrefix(), dto.getNumberLength(), dto.getQuantity()));
    }

}

