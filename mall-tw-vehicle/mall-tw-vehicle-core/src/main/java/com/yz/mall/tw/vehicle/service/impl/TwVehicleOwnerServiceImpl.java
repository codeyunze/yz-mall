package com.yz.mall.tw.vehicle.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.sys.service.ExtendSysUserService;
import com.yz.mall.sys.vo.ExtendLoginInfoVo;
import com.yz.mall.tw.vehicle.constant.TwVehicleConstants;
import com.yz.mall.tw.vehicle.dto.TwVehicleOwnerBindDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleOwnerTransferDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleOwnerUnbindDto;
import com.yz.mall.tw.vehicle.entity.TwVehicle;
import com.yz.mall.tw.vehicle.entity.TwVehicleOwner;
import com.yz.mall.tw.vehicle.mapper.TwVehicleOwnerMapper;
import com.yz.mall.tw.vehicle.service.TwVehicleAuthService;
import com.yz.mall.tw.vehicle.service.TwVehicleOwnerService;
import com.yz.mall.tw.vehicle.service.TwVehicleService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 车主绑定服务实现
 */
@Service
public class TwVehicleOwnerServiceImpl extends ServiceImpl<TwVehicleOwnerMapper, TwVehicleOwner> implements TwVehicleOwnerService {

    private final TwVehicleService vehicleService;
    private final TwVehicleAuthService authService;
    private final ExtendSysUserService extendSysUserService;

    public TwVehicleOwnerServiceImpl(@Lazy TwVehicleService vehicleService, @Lazy TwVehicleAuthService authService,
                                     ExtendSysUserService extendSysUserService) {
        this.vehicleService = vehicleService;
        this.authService = authService;
        this.extendSysUserService = extendSysUserService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long bind(TwVehicleOwnerBindDto dto) {
        TwVehicle vehicle = vehicleService.requireVehicle(dto.getVehicleId());
        if (!Objects.equals(vehicle.getStatus(), TwVehicleConstants.STATUS_ENABLED)) {
            throw new BusinessException("车辆已停用，无法绑定车主");
        }
        requireUserExists(dto.getOwnerUserId());
        TwVehicleOwner active = getActiveOwner(dto.getVehicleId());
        if (active != null) {
            if (Objects.equals(active.getOwnerUserId(), dto.getOwnerUserId())) {
                return active.getId();
            }
            throw new BusinessException("请先解绑当前车主或使用过户");
        }
        TwVehicleOwner owner = new TwVehicleOwner();
        owner.setId(IdUtil.getSnowflakeNextId());
        owner.setVehicleId(vehicle.getId());
        owner.setVin(vehicle.getVin());
        owner.setOwnerUserId(dto.getOwnerUserId());
        owner.setBindStatus(TwVehicleConstants.BIND_STATUS_ACTIVE);
        owner.setBindTime(LocalDateTime.now());
        owner.setBindSource(TwVehicleConstants.BIND_SOURCE_OPERATOR);
        owner.setRemark(dto.getRemark());
        owner.setCreateId(StpUtil.getLoginIdAsLong());
        owner.setUpdateId(owner.getCreateId());
        save(owner);
        return owner.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unbind(TwVehicleOwnerUnbindDto dto) {
        TwVehicleOwner active = getActiveOwner(dto.getVehicleId());
        if (active == null) {
            throw new BusinessException("当前车辆无有效车主");
        }
        Long operatorId = StpUtil.getLoginIdAsLong();
        authService.revokeAllActive(dto.getVehicleId(), operatorId);
        active.setBindStatus(TwVehicleConstants.BIND_STATUS_UNBOUND);
        active.setUnbindTime(LocalDateTime.now());
        active.setRemark(dto.getRemark());
        active.setUpdateId(operatorId);
        return updateById(active);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long transfer(TwVehicleOwnerTransferDto dto) {
        TwVehicle vehicle = vehicleService.requireVehicle(dto.getVehicleId());
        if (!Objects.equals(vehicle.getStatus(), TwVehicleConstants.STATUS_ENABLED)) {
            throw new BusinessException("车辆已停用，无法过户");
        }
        requireUserExists(dto.getNewOwnerUserId());
        Long operatorId = StpUtil.getLoginIdAsLong();
        authService.revokeAllActive(dto.getVehicleId(), operatorId);

        TwVehicleOwner active = getActiveOwner(dto.getVehicleId());
        if (active != null) {
            if (Objects.equals(active.getOwnerUserId(), dto.getNewOwnerUserId())) {
                return active.getId();
            }
            active.setBindStatus(TwVehicleConstants.BIND_STATUS_UNBOUND);
            active.setUnbindTime(LocalDateTime.now());
            active.setUpdateId(operatorId);
            updateById(active);
        }

        TwVehicleOwner owner = new TwVehicleOwner();
        owner.setId(IdUtil.getSnowflakeNextId());
        owner.setVehicleId(vehicle.getId());
        owner.setVin(vehicle.getVin());
        owner.setOwnerUserId(dto.getNewOwnerUserId());
        owner.setBindStatus(TwVehicleConstants.BIND_STATUS_ACTIVE);
        owner.setBindTime(LocalDateTime.now());
        owner.setBindSource(TwVehicleConstants.BIND_SOURCE_TRANSFER);
        owner.setRemark(dto.getRemark());
        owner.setCreateId(operatorId);
        owner.setUpdateId(operatorId);
        save(owner);
        return owner.getId();
    }

    @Override
    public TwVehicleOwner getActiveOwner(Long vehicleId) {
        return getOne(new LambdaQueryWrapper<TwVehicleOwner>()
                .eq(TwVehicleOwner::getVehicleId, vehicleId)
                .eq(TwVehicleOwner::getBindStatus, TwVehicleConstants.BIND_STATUS_ACTIVE)
                .last("limit 1"), false);
    }

    /**
     * 车主名下有效车辆 ID
     */
    @Override
    public Set<Long> listActiveVehicleIdsByOwner(Long ownerUserId) {
        List<TwVehicleOwner> list = list(new LambdaQueryWrapper<TwVehicleOwner>()
                .eq(TwVehicleOwner::getOwnerUserId, ownerUserId)
                .eq(TwVehicleOwner::getBindStatus, TwVehicleConstants.BIND_STATUS_ACTIVE)
                .select(TwVehicleOwner::getVehicleId));
        return list.stream().map(TwVehicleOwner::getVehicleId).collect(Collectors.toSet());
    }

    /**
     * 批量查当前有效车主
     */
    @Override
    public Map<Long, TwVehicleOwner> listActiveByVehicleIds(List<Long> vehicleIds) {
        if (vehicleIds == null || vehicleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<TwVehicleOwner> list = list(new LambdaQueryWrapper<TwVehicleOwner>()
                .in(TwVehicleOwner::getVehicleId, vehicleIds)
                .eq(TwVehicleOwner::getBindStatus, TwVehicleConstants.BIND_STATUS_ACTIVE));
        return list.stream().collect(Collectors.toMap(TwVehicleOwner::getVehicleId, Function.identity(), (a, b) -> a));
    }

    private void requireUserExists(Long userId) {
        ExtendLoginInfoVo info = extendSysUserService.getUserInfoById(userId);
        if (info == null || info.getId() == null) {
            throw new BusinessException("用户不存在");
        }
    }
}
