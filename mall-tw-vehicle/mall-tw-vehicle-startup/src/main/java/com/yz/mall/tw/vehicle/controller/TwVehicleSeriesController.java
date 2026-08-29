package com.yz.mall.tw.vehicle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.tw.vehicle.dto.TwVehicleMasterStatusDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleSeriesAddDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleSeriesQueryDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleSeriesUpdateDto;
import com.yz.mall.tw.vehicle.service.TwVehicleSeriesService;
import com.yz.mall.tw.vehicle.vo.TwVehicleSeriesOptionVo;
import com.yz.mall.tw.vehicle.vo.TwVehicleSeriesVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 车系管理
 */
@RestController
@RequestMapping("tw/series")
public class TwVehicleSeriesController extends ApiController {

    private final TwVehicleSeriesService seriesService;

    public TwVehicleSeriesController(TwVehicleSeriesService seriesService) {
        this.seriesService = seriesService;
    }

    /**
     * 分页
     */
    @PostMapping("page")
    public Result<ResultTable<TwVehicleSeriesVo>> page(@RequestBody @Valid PageFilter<TwVehicleSeriesQueryDto> filter) {
        Page<TwVehicleSeriesVo> page = seriesService.pageSeries(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情
     */
    @GetMapping("{id}")
    public Result<TwVehicleSeriesVo> detail(@PathVariable Long id) {
        return success(seriesService.detail(id));
    }

    /**
     * 新建
     */
    @PostMapping
    public Result<Long> add(@RequestBody @Valid TwVehicleSeriesAddDto dto) {
        return success(seriesService.add(dto));
    }

    /**
     * 编辑
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody @Valid TwVehicleSeriesUpdateDto dto) {
        return success(seriesService.edit(dto));
    }

    /**
     * 启停
     */
    @PutMapping("status")
    public Result<Boolean> status(@RequestBody @Valid TwVehicleMasterStatusDto dto) {
        return success(seriesService.changeStatus(dto));
    }

    /**
     * 删除
     */
    @DeleteMapping("{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(seriesService.deleteSeries(id));
    }

    /**
     * 启用车系下拉
     */
    @GetMapping("options")
    public Result<List<TwVehicleSeriesOptionVo>> options() {
        return success(seriesService.options());
    }
}
