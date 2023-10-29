package com.yz.dynamic.datasource.one.controller;


import com.yz.dynamic.datasource.one.dto.ProductAddDto;
import com.yz.dynamic.datasource.one.service.TProductService;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 商品信息(TProduct)表控制层
 *
 * @author yunze
 * @since 2023-10-29 18:01:13
 */
@RestController
@RequestMapping("tProduct")
public class TProductController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private TProductService tProductService;


    /**
     * 新增数据
     *
     * @param dto 实体对象
     * @return 新增结果
     */
    @PostMapping("/add")
    public Result<Boolean> insert(@RequestBody @Validated ProductAddDto dto) {
        return success(this.tProductService.save(dto) > 0);
    }

}

