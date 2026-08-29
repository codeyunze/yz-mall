package com.yz.mall.tw.vehicle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.tw.vehicle.dto.TwVehicleAddDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleAuthGrantDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleAuthRevokeDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleOwnerBindDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleOwnerTransferDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleOwnerUnbindDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleQueryDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleStatusDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleUpdateDto;
import com.yz.mall.tw.vehicle.service.TwVehicleAuthService;
import com.yz.mall.tw.vehicle.service.TwVehicleOwnerService;
import com.yz.mall.tw.vehicle.service.TwVehicleService;
import com.yz.mall.tw.vehicle.vo.TwVehicleAuthVo;
import com.yz.mall.tw.vehicle.vo.TwVehicleDetailVo;
import com.yz.mall.tw.vehicle.vo.TwVehiclePageVo;
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
 * 车辆档案对外接口（经网关 /tw/vehicle/**）
 */
@RestController
@RequestMapping("tw/vehicle")
public class TwVehicleController extends ApiController {

    private final TwVehicleService vehicleService;
    private final TwVehicleOwnerService ownerService;
    private final TwVehicleAuthService authService;

    public TwVehicleController(TwVehicleService vehicleService, TwVehicleOwnerService ownerService, TwVehicleAuthService authService) {
        this.vehicleService = vehicleService;
        this.ownerService = ownerService;
        this.authService = authService;
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<ResultTable<TwVehiclePageVo>> page(@RequestBody @Valid PageFilter<TwVehicleQueryDto> filter) {
        Page<TwVehiclePageVo> page = vehicleService.pageVehicles(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情
     */
    @GetMapping("{id}")
    public Result<TwVehicleDetailVo> detail(@PathVariable Long id) {
        return success(vehicleService.detail(id));
    }

    /**
     * 新建
     */
    @PostMapping
    public Result<Long> add(@RequestBody @Valid TwVehicleAddDto dto) {
        return success(vehicleService.add(dto));
    }

    /**
     * 编辑
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody @Valid TwVehicleUpdateDto dto) {
        return success(vehicleService.edit(dto));
    }

    /**
     * 启用/停用
     */
    @PutMapping("status")
    public Result<Boolean> status(@RequestBody @Valid TwVehicleStatusDto dto) {
        return success(vehicleService.changeStatus(dto));
    }

    /**
     * 逻辑删除
     */
    @DeleteMapping("{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return success(vehicleService.deleteVehicle(id));
    }

    /**
     * 绑定车主
     */
    @PostMapping("owner/bind")
    public Result<Long> bindOwner(@RequestBody @Valid TwVehicleOwnerBindDto dto) {
        return success(ownerService.bind(dto));
    }

    /**
     * 解绑车主
     */
    @PostMapping("owner/unbind")
    public Result<Boolean> unbindOwner(@RequestBody @Valid TwVehicleOwnerUnbindDto dto) {
        return success(ownerService.unbind(dto));
    }

    /**
     * 过户
     */
    @PostMapping("owner/transfer")
    public Result<Long> transferOwner(@RequestBody @Valid TwVehicleOwnerTransferDto dto) {
        return success(ownerService.transfer(dto));
    }

    /**
     * 授权用户
     */
    @PostMapping("auth/grant")
    public Result<Long> grantAuth(@RequestBody @Valid TwVehicleAuthGrantDto dto) {
        return success(authService.grant(dto));
    }

    /**
     * 撤销授权
     */
    @PostMapping("auth/revoke")
    public Result<Boolean> revokeAuth(@RequestBody @Valid TwVehicleAuthRevokeDto dto) {
        return success(authService.revoke(dto));
    }

    /**
     * 授权用户列表
     */
    @GetMapping("{vehicleId}/auth/list")
    public Result<List<TwVehicleAuthVo>> listAuth(@PathVariable Long vehicleId,
                                                  @RequestParam(defaultValue = "false") boolean includeHistory) {
        return success(authService.listAuth(vehicleId, includeHistory));
    }
}
