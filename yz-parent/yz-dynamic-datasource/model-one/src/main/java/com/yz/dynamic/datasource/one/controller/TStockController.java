package com.yz.dynamic.datasource.one.controller;


import com.yz.dynamic.datasource.one.dto.StockAddDto;
import com.yz.dynamic.datasource.one.service.TStockService;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 库存信息(TStock)表控制层
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
@RestController
@RequestMapping("tStock")
public class TStockController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private TStockService tStockService;

    /**
     * 新增数据
     *
     * @param dto 实体对象
     * @return 新增结果
     */
    @PostMapping("/add")
    public Result<Boolean> insert(@RequestBody @Validated StockAddDto dto) {
        return success(this.tStockService.save(dto) > 0);
    }

}

