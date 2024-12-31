package com.yz.unqid.controller;


import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import com.yz.unqid.dto.InternalUnqidDto;
import com.yz.unqid.service.SysUnqidService;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("internal/unqid/v3/")
public class InternalSysUnqidV3Controller extends ApiController {

    @Value("${server.port}")
    private String port;


    @Resource(name = "sysUnqidV3ServiceImpl")
    private SysUnqidService service;

    /**
     * 生成流水号
     */
    @PostMapping("generateSerialNumber")
    public Result<String> generateSerialNumber(@RequestBody @Valid InternalUnqidDto dto) {
        System.out.println("节点：" + port);
        return success(this.service.generateSerialNumber(dto.getPrefix(), dto.getNumberLength()));
    }
}

