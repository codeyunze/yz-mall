package com.yz.cases.mall.controller;


import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.yz.cases.mall.dto.ProductAddDto;
import com.yz.cases.mall.entity.TProduct;
import com.yz.cases.mall.service.TProductService;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    private DynamicRoutingDataSource dataSource;

    /**
     * 新增数据
     *
     * @param dto 实体对象
     * @return 新增结果
     */
    @PostMapping("/add")
    public Result<Boolean> insert(@RequestBody @Validated ProductAddDto dto, HttpServletRequest request) {
        request.getSession().setAttribute("rw", dto.getDbSource());
        return success(this.tProductService.save(dto) > 0);
    }

    /**
     * 列表查询
     */
    @PostMapping("list/{key}")
    public Result<List<TProduct>> list(@PathVariable String key) {
        // DynamicDataSource.name.set(DataSourceTypeEnum.product.get());
        return new Result<>(CodeEnum.SUCCESS.get(), this.tProductService.customList(), "成功");
    }


}

