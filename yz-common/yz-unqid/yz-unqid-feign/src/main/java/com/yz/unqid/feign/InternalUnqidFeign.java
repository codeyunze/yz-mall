package com.yz.unqid.feign;

import com.yz.tools.Result;
import com.yz.unqid.dto.InternalUnqidDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yunze
 * @date 2024/6/24 星期一 23:36
 */
@FeignClient(name = "yz-unqid", contextId = "internalUnqid")
@RequestMapping("internal/baseUser")
public interface InternalUnqidFeign {

    /**
     * 生成流水号
     */
    @PostMapping("generateSerialNumber")
    public Result<String> generateSerialNumber(@RequestBody @Valid InternalUnqidDto dto);

    /**
     * 批量生成流水号
     */
    @PostMapping("generateSerialNumbers")
    public Result<List<String>> generateSerialNumbers(@RequestBody @Valid InternalUnqidDto dto);
}
