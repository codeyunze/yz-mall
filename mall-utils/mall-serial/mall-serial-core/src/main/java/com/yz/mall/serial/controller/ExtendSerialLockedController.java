package com.yz.mall.serial.controller;


import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.serial.dto.ExtendUnqidDto;
import com.yz.mall.serial.service.SerialService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 系统-流水号表(SysUnqid)表控制层
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@RestController
@RequestMapping("extend/serial/v2/")
public class ExtendSerialLockedController extends ApiController {


    @Resource(name = "serialLockedServiceImpl")
    private SerialService service;

    /**
     * 生成流水号
     */
    @PostMapping("generateNumber")
    public Result<String> generateNumber(@RequestBody @Valid ExtendUnqidDto dto) {
        return success(this.service.generateSerialNumber(dto.getPrefix(), dto.getNumberLength()));
    }
}

