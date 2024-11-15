package com.yz.unqid.controller;


import com.yz.tools.ApiController;
import com.yz.tools.Result;
import com.yz.unqid.dto.InternalUnqidDto;
import com.yz.unqid.entity.SysUnqid;
import com.yz.unqid.service.InternalUnqidService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统-流水号表(SysUnqid)表控制层
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
@RestController
@RequestMapping("internal/unqid/")
public class InternalSysUnqidController extends ApiController {

    private final InternalUnqidService service;

    public InternalSysUnqidController(InternalUnqidService service) {
        this.service = service;
    }


    /**
     * 生成流水号
     */
    @PostMapping("generateSerialNumber")
    public Result<String> generateSerialNumber(@RequestBody @Valid InternalUnqidDto dto) throws InterruptedException {
        // Thread.sleep(30000);
        return success(this.service.generateSerialNumber(dto.getPrefix(), dto.getNumberLength()));
    }

    /**
     * 批量生成流水号
     */
    @PostMapping("generateSerialNumbers")
    public Result<List<String>> generateSerialNumbers(@RequestBody @Valid InternalUnqidDto dto) {
        return success(this.service.generateSerialNumbers(dto.getPrefix(), dto.getNumberLength(), dto.getQuantity()));
    }

}

