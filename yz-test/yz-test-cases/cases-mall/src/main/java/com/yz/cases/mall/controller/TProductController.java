package com.yz.cases.mall.controller;


import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.cases.mall.dto.TProductAddDto;
import com.yz.cases.mall.dto.TProductQueryDto;
import com.yz.cases.mall.dto.TProductUpdateDto;
import com.yz.cases.mall.entity.SysDatasource;
import com.yz.cases.mall.entity.TProduct;
import com.yz.cases.mall.service.SysDatasourceService;
import com.yz.cases.mall.service.TProductService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 商品信息(TProduct)表控制层
 *
 * @author yunze
 * @since 2024-06-13 08:38:51
 */
@Slf4j
@RestController
@RequestMapping("product")
public class TProductController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private TProductService service;

    @Resource
    private DynamicRoutingDataSource dataSource;

    @Resource
    private SysDatasourceService sysDatasourceService;

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid TProductAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid TProductUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<List<TProduct>> page(@RequestBody @Valid PageFilter<TProductQueryDto> filter, HttpServletRequest request) {
        String datasourceId = filter.getFilter().getDatasourceId();
        if (!dataSource.getDataSources().containsKey(datasourceId)) {
            SysDatasource sysDatasource = sysDatasourceService.getById(datasourceId);

            DruidDataSource db = new DruidDataSource();
            db.setUsername(sysDatasource.getUsername());
            db.setPassword(sysDatasource.getPassword());
            db.setDriverClassName(sysDatasource.getDriverClassName());
            db.setUrl(sysDatasource.getUrl());
            dataSource.addDataSource(datasourceId, db);
            log.info("Added datasource {}", datasourceId);
        } else {
            log.info("Already exists datasource {}", datasourceId);
        }

        request.getSession().setAttribute("rw", datasourceId);

        Page<TProduct> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<TProduct> page(@PathVariable Long id) {
        return success(this.service.getById(id));
    }

}

