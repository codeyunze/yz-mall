package com.yz.mall.serial.controller;


import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.serial.dto.ExtendSerialDto;
import com.yz.mall.serial.service.SerialService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 系统-流水号表(SysUnqid)表控制层
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@RestController
@RequestMapping("extend/serial/v3/")
public class ExtendSerialPoolController extends ApiController {

    @Value("${server.port}")
    private String port;


    @Resource(name = "serialPoolServiceImpl")
    private SerialService service;

    /**
     * 生成流水号
     */
    @PostMapping("generateNumber")
    public Result<String> generateNumber(@RequestBody @Valid ExtendSerialDto dto) {
        System.out.println("节点：" + port);
        return success(this.service.generateSerialNumber(dto.getPrefix(), dto.getNumberLength()));
    }
}

