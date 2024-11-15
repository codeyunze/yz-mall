package com.yz.mall.oms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.oms.dto.OmsOrderAddDto;
import com.yz.mall.oms.dto.OmsOrderGenerateDto;
import com.yz.mall.oms.dto.OmsOrderQueryDto;
import com.yz.mall.oms.dto.OmsOrderUpdateDto;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.mall.oms.service.OmsOrderService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.unqid.service.InternalUnqidService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 订单信息表(OmsOrder)表控制层
 *
 * @author yunze
 * @since 2024-06-18 12:49:54
 */
@Slf4j
@RestController
@RequestMapping("oms/order")
public class OmsOrderController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private OmsOrderService service;

    /**
     * 测试
     */
    @CircuitBreaker(name = "OmsOrderController", fallbackMethod = "omsCircuitFallback")
    @RequestMapping("test")
    public Result<String> test() {
        log.info("生成序列号");
        String serialNumber = internalUnqidService.generateSerialNumber("ABC241105", 6);
        log.info("serialNumber:{}", serialNumber);
        return success(serialNumber);
    }

    @Resource
    private InternalUnqidService internalUnqidService;

    /**
     * omsCircuitFallback就是服务降级后的兜底处理方法
     *
     * @param t
     * @return
     */
    public Result<String> omsCircuitFallback(Throwable t) {
        // 这里是容错处理逻辑，返回备用结果
        return failed("omsCircuitFallback，系统繁忙，请稍后再试-----/(ㄒoㄒ)/~~");
    }

    /**
     * 生成订单
     */
    @PostMapping("generate")
    public Result<String> generateOrder(@RequestBody @Valid OmsOrderGenerateDto dto) {
        return success(this.service.generateOrder(dto));
    }


    /**
     * 新增
     */
    @Deprecated
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid OmsOrderAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @Deprecated
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid OmsOrderUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @Deprecated
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<List<OmsOrder>> page(@RequestBody @Valid PageFilter<OmsOrderQueryDto> filter) {
        Page<OmsOrder> page = this.service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<OmsOrder> page(@PathVariable String id) {
        return success(this.service.getById(id));
    }

}

