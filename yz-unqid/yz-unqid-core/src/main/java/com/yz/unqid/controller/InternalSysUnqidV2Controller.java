package com.yz.unqid.controller;


import com.yz.tools.ApiController;
import com.yz.tools.Result;
import com.yz.unqid.dto.InternalUnqidDto;
import com.yz.unqid.service.InternalUnqidService;
import com.yz.unqid.service.SysUnqidService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 系统-流水号表(SysUnqid)表控制层
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@RestController
@RequestMapping("internal/unqid/v2/")
public class InternalSysUnqidV2Controller extends ApiController {


    @Resource(name = "sysUnqidV2ServiceImpl")
    private SysUnqidService service;

    /**
     * 生成流水号
     */
    @PostMapping("generateSerialNumber")
    public Result<String> generateSerialNumber(@RequestBody @Valid InternalUnqidDto dto) {
        return success(this.service.generateSerialNumber(dto.getPrefix(), dto.getNumberLength()));
    }
}

