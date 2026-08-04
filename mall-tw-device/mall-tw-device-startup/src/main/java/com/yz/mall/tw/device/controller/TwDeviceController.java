package com.yz.mall.tw.device.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.tw.device.dto.TwDeviceAddDto;
import com.yz.mall.tw.device.dto.TwDeviceBindDto;
import com.yz.mall.tw.device.dto.TwDeviceCredResetDto;
import com.yz.mall.tw.device.dto.TwDeviceQueryDto;
import com.yz.mall.tw.device.dto.TwDeviceStatusDto;
import com.yz.mall.tw.device.dto.TwDeviceUnbindDto;
import com.yz.mall.tw.device.dto.TwDeviceUpdateDto;
import com.yz.mall.tw.device.service.TwDeviceService;
import com.yz.mall.tw.device.vo.TwDeviceCreateVo;
import com.yz.mall.tw.device.vo.TwDeviceCredResetVo;
import com.yz.mall.tw.device.vo.TwDeviceDetailVo;
import com.yz.mall.tw.device.vo.TwDevicePageVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 终端管理对外接口（经网关 /tw/device/**）
 */
@RestController
@RequestMapping("tw/device")
public class TwDeviceController extends ApiController {

    private final TwDeviceService deviceService;

    public TwDeviceController(TwDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @SaCheckPermission("api:tw:device:page")
    @PostMapping("page")
    public Result<ResultTable<TwDevicePageVo>> page(@RequestBody @Valid PageFilter<TwDeviceQueryDto> filter) {
        Page<TwDevicePageVo> page = deviceService.pageDevices(filter);
        return success(page.getRecords(), page.getTotal());
    }

    @SaCheckPermission("api:tw:device:detail")
    @GetMapping("{id}")
    public Result<TwDeviceDetailVo> detail(@PathVariable Long id) {
        return success(deviceService.detail(id));
    }

    @SaCheckPermission("api:tw:device:add")
    @PostMapping
    public Result<TwDeviceCreateVo> add(@RequestBody TwDeviceAddDto dto) {
        return success(deviceService.register(dto));
    }

    @SaCheckPermission("api:tw:device:edit")
    @PutMapping
    public Result<Boolean> edit(@RequestBody @Valid TwDeviceUpdateDto dto) {
        return success(deviceService.edit(dto));
    }

    @SaCheckPermission("api:tw:device:status")
    @PutMapping("status")
    public Result<Boolean> status(@RequestBody @Valid TwDeviceStatusDto dto) {
        return success(deviceService.changeStatus(dto));
    }

    @SaCheckPermission("api:tw:device:delete")
    @DeleteMapping("{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(deviceService.deleteDevice(id));
    }

    @SaCheckPermission("api:tw:device:bind")
    @PostMapping("bind")
    public Result<Long> bind(@RequestBody TwDeviceBindDto dto) {
        return success(deviceService.bind(dto));
    }

    @SaCheckPermission("api:tw:device:unbind")
    @PostMapping("unbind")
    public Result<Boolean> unbind(@RequestBody TwDeviceUnbindDto dto) {
        return success(deviceService.unbind(dto));
    }

    @SaCheckPermission("api:tw:device:cred:reset")
    @PostMapping("cred/reset")
    public Result<TwDeviceCredResetVo> resetCred(@RequestBody TwDeviceCredResetDto dto) {
        return success(deviceService.resetCredential(dto));
    }
}
