package com.yz.dynamic.datasource.one.controller;


import com.alibaba.druid.pool.DruidDataSource;
import com.yz.dynamic.datasource.one.config.DynamicDataSource;
import com.yz.dynamic.datasource.one.dto.ProductAddDto;
import com.yz.dynamic.datasource.one.entity.TProduct;
import com.yz.dynamic.datasource.one.service.TProductService;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import com.yz.tools.enums.DataSourceTypeEnum;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品信息(TProduct)表控制层
 *
 * @author yunze
 * @since 2023-10-29 18:01:13
 */
@RestController
@RequestMapping("product")
public class TProductController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private TProductService tProductService;

    @Resource
    private DynamicDataSource dynamicDataSource;


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

    /**
     * 列表查询
     */
    @PostMapping("list/{key}")
    public Result<List<TProduct>> list(@PathVariable String key) {
        DynamicDataSource.name.set(key);
        // DynamicDataSource.name.set(DataSourceTypeEnum.product.get());
        return new Result<>(CodeEnum.SUCCESS.get(), this.tProductService.customList(), "成功");
    }

    @RequestMapping("/db/{key}")
    public String addDb(@PathVariable String key) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mall_test?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF8&useSSL=false&allowPublicKeyRetrieval=true");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dynamicDataSource.addDataSource(key, dataSource);
        return "success";
    }

}

