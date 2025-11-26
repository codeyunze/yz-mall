package com.yz.mall.so.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.so.dto.SeataOrderAddDto;
import com.yz.mall.so.dto.SeataOrderQueryDto;
import com.yz.mall.so.dto.SeataOrderUpdateDto;
import com.yz.mall.so.entity.SeataOrder;
import com.yz.mall.so.service.SeataOrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


/**
 * 分布式事务-订单表(SeataOrder)表控制层
 *
 * @author yunze
 * @since 2025-11-24 22:40:03
 */
@RestController
@RequestMapping("seataOrder")
public class SeataOrderController extends ApiController {

    /**
     * 服务对象
     */
    private final SeataOrderService service;

    public SeataOrderController(SeataOrderService service) {
        this.service = service;
    }

    /**
     * 新增
     */
    @PostMapping("add")
    public Result<Long> insert(@RequestBody @Valid SeataOrderAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SeataOrderUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键 ID
     */
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<ResultTable<SeataOrder>> page(@RequestBody @Valid PageFilter<SeataOrderQueryDto> filter) {
        Page<SeataOrder> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SeataOrder> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

