package com.yz.mall.tw.vehicle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.tw.vehicle.dto.TwVehicleMasterStatusDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleModelAddDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleModelQueryDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleModelUpdateDto;
import com.yz.mall.tw.vehicle.service.TwVehicleModelService;
import com.yz.mall.tw.vehicle.vo.TwVehicleModelOptionVo;
import com.yz.mall.tw.vehicle.vo.TwVehicleModelVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 车型管理
 */
@RestController
@RequestMapping("tw/model")
public class TwVehicleModelController extends ApiController {

    private final TwVehicleModelService modelService;

    public TwVehicleModelController(TwVehicleModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * 分页
     */
    @PostMapping("page")
    public Result<ResultTable<TwVehicleModelVo>> page(@RequestBody @Valid PageFilter<TwVehicleModelQueryDto> filter) {
        Page<TwVehicleModelVo> page = modelService.pageModels(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情
     */
    @GetMapping("{id}")
    public Result<TwVehicleModelVo> detail(@PathVariable Long id) {
        return success(modelService.detail(id));
    }

    /**
     * 新建
     */
    @PostMapping
    public Result<Long> add(@RequestBody @Valid TwVehicleModelAddDto dto) {
        return success(modelService.add(dto));
    }

    /**
     * 编辑
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody @Valid TwVehicleModelUpdateDto dto) {
        return success(modelService.edit(dto));
    }

    /**
     * 启停
     */
    @PutMapping("status")
    public Result<Boolean> status(@RequestBody @Valid TwVehicleMasterStatusDto dto) {
        return success(modelService.changeStatus(dto));
    }

    /**
     * 删除
     */
    @DeleteMapping("{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(modelService.deleteModel(id));
    }

    /**
     * 按车系启用车型下拉（建档联动）
     */
    @GetMapping("options")
    public Result<List<TwVehicleModelOptionVo>> options(@RequestParam(required = false) Long seriesId,
                                                        @RequestParam(required = false) String seriesCode) {
        return success(modelService.options(seriesId, seriesCode));
    }
}
