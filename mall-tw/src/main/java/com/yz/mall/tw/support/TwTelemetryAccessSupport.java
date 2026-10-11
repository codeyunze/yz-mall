package com.yz.mall.tw.support;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.tw.constant.TwTelemetryConstants;
import com.yz.mall.tw.constant.TwVehicleConstants;
import com.yz.mall.tw.service.ExtendTwVehicleService;
import com.yz.mall.tw.vo.ExtendTwVehicleAccessVo;
import org.springframework.stereotype.Component;

/**
 * 遥测查询数据范围：运营跳过绑定；车主/授权走 access/check（scope=位置）
 */
@Component
public class TwTelemetryAccessSupport {

    private final ExtendTwVehicleService extendTwVehicleService;

    public TwTelemetryAccessSupport(ExtendTwVehicleService extendTwVehicleService) {
        this.extendTwVehicleService = extendTwVehicleService;
    }

    /**
     * 是否具备运营/监控级全量车辆位置权限（以批量权限码为准）。
     *
     * @return true 可跳过车主/授权绑定校验
     */
    public boolean isFleetOperator() {
        if (!StpUtil.isLogin()) {
            return false;
        }
        return StpUtil.hasPermission(TwTelemetryConstants.PERM_LATEST_BATCH)
                || StpUtil.hasPermission("api:tw:vehicle:owner:bind");
    }

    /**
     * 校验当前登录用户对指定车辆的位置访问（scope=2）。
     *
     * @param vin       车架号（可与 vehicleId 二选一）
     * @param vehicleId 车辆 ID
     */
    public void assertLocationAccess(String vin, Long vehicleId) {
        if (isFleetOperator()) {
            return;
        }
        if (!StpUtil.isLogin()) {
            throw new BusinessException("无权查看该车辆位置");
        }
        if (StrUtil.isBlank(vin) && vehicleId == null) {
            throw new BusinessException("请指定 VIN 或车辆ID");
        }
        long userId = StpUtil.getLoginIdAsLong();
        ExtendTwVehicleAccessVo access = extendTwVehicleService.checkAccess(vehicleId, vin, userId, TwVehicleConstants.SCOPE_LOCATION);
        if (access == null || !Boolean.TRUE.equals(access.getAllowed())) {
            throw new BusinessException("无权查看该车辆位置");
        }
    }
}
