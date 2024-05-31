package com.yz.cases.mall.controller;


import com.yz.cases.mall.dto.TStorageAddDto;
import com.yz.cases.mall.entity.TStorage;
import com.yz.cases.mall.service.TStorageService;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 库存信息(TStock)表控制层
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
@RestController
@RequestMapping("stock")
public class TStorageController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private TStorageService tStockService;

    /**
     * 新增数据
     *
     * @param dto 实体对象
     * @return 新增结果
     */
    @PostMapping("/add")
    public Result<Boolean> insert(@RequestBody @Validated TStorageAddDto dto) {
        return success(this.tStockService.save(dto) > 0);
    }

    @PostMapping("list")
    public Result<List<TStorage>> list() {
        return new Result<>(CodeEnum.SUCCESS.get(), this.tStockService.list(), "成功");
    }
}

