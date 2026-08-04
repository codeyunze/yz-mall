package com.yz.mall.tw.vehicle.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.IdsDto;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.sys.service.ExtendSysUserService;
import com.yz.mall.sys.vo.ExtendLoginInfoVo;
import com.yz.mall.sys.vo.ExtendSysUserSlimVo;
import com.yz.mall.tw.device.service.ExtendTwDeviceService;
import com.yz.mall.tw.device.vo.ExtendTwDeviceSlimVo;
import com.yz.mall.tw.vehicle.constant.TwVehicleConstants;
import com.yz.mall.tw.vehicle.dto.TwVehicleAddDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleQueryDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleStatusDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleUpdateDto;
import com.yz.mall.tw.vehicle.entity.TwVehicle;
import com.yz.mall.tw.vehicle.entity.TwVehicleAuth;
import com.yz.mall.tw.vehicle.entity.TwVehicleOwner;
import com.yz.mall.tw.vehicle.mapper.TwVehicleMapper;
import com.yz.mall.tw.vehicle.service.TwVehicleAuthService;
import com.yz.mall.tw.vehicle.service.TwVehicleOwnerService;
import com.yz.mall.tw.vehicle.service.TwVehicleService;
import com.yz.mall.tw.vehicle.support.TwVehicleRealtimeSupport;
import com.yz.mall.tw.vehicle.vo.TwVehicleDetailVo;
import com.yz.mall.tw.vehicle.vo.TwVehicleDeviceSummaryVo;
import com.yz.mall.tw.vehicle.vo.TwVehicleOwnerVo;
import com.yz.mall.tw.vehicle.vo.TwVehiclePageVo;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 车辆档案服务实现
 */
@Service
public class TwVehicleServiceImpl extends ServiceImpl<TwVehicleMapper, TwVehicle> implements TwVehicleService {

    private final TwVehicleOwnerService ownerService;
    private final TwVehicleAuthService authService;
    private final ExtendSysUserService extendSysUserService;
    private final TwVehicleRealtimeSupport realtimeSupport;
    private final ObjectProvider<ExtendTwDeviceService> extendTwDeviceService;

    public TwVehicleServiceImpl(@Lazy TwVehicleOwnerService ownerService, @Lazy TwVehicleAuthService authService,
                                ExtendSysUserService extendSysUserService, TwVehicleRealtimeSupport realtimeSupport,
                                ObjectProvider<ExtendTwDeviceService> extendTwDeviceService) {
        this.ownerService = ownerService;
        this.authService = authService;
        this.extendSysUserService = extendSysUserService;
        this.realtimeSupport = realtimeSupport;
        this.extendTwDeviceService = extendTwDeviceService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(TwVehicleAddDto dto) {
        String vin = dto.getVin().trim().toUpperCase();
        long exists = count(new LambdaQueryWrapper<TwVehicle>().eq(TwVehicle::getVin, vin));
        if (exists > 0) {
            throw new BusinessException("VIN 已建档");
        }
        TwVehicle entity = new TwVehicle();
        BeanUtil.copyProperties(dto, entity);
        entity.setId(IdUtil.getSnowflakeNextId());
        entity.setVin(vin);
        entity.setStatus(dto.getStatus() == null ? TwVehicleConstants.STATUS_ENABLED : dto.getStatus());
        entity.setCreateId(currentUserId());
        entity.setUpdateId(entity.getCreateId());
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean edit(TwVehicleUpdateDto dto) {
        TwVehicle entity = requireVehicle(dto.getId());
        if (dto.getPlateNo() != null) {
            entity.setPlateNo(dto.getPlateNo());
        }
        if (dto.getModelCode() != null) {
            entity.setModelCode(dto.getModelCode());
        }
        if (dto.getModelName() != null) {
            entity.setModelName(dto.getModelName());
        }
        if (dto.getColor() != null) {
            entity.setColor(dto.getColor());
        }
        if (dto.getRemark() != null) {
            entity.setRemark(dto.getRemark());
        }
        if (dto.getCoverFileId() != null) {
            entity.setCoverFileId(dto.getCoverFileId());
        }
        entity.setUpdateId(currentUserId());
        return updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeStatus(TwVehicleStatusDto dto) {
        if (!Objects.equals(dto.getStatus(), TwVehicleConstants.STATUS_ENABLED) && !Objects.equals(dto.getStatus(), TwVehicleConstants.STATUS_DISABLED)) {
            throw new BusinessException("状态值非法");
        }
        TwVehicle entity = requireVehicle(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setUpdateId(currentUserId());
        return updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteVehicle(Long id) {
        TwVehicle entity = requireVehicle(id);
        if (ownerService.getActiveOwner(id) != null) {
            throw new BusinessException("请先解绑车主后再删除");
        }
        if (authService.countActive(id) > 0) {
            throw new BusinessException("请先撤销全部授权后再删除");
        }
        // 逻辑删除：invalid 置为行 id，释放 VIN 再建档能力
        return lambdaUpdate().set(TwVehicle::getInvalid, id).set(TwVehicle::getUpdateId, currentUserId()).eq(TwVehicle::getId, id).eq(TwVehicle::getInvalid, 0L).update();
    }

    @Override
    public Page<TwVehiclePageVo> pageVehicles(PageFilter<TwVehicleQueryDto> filter) {
        TwVehicleQueryDto query = filter.getFilter() == null ? new TwVehicleQueryDto() : filter.getFilter();
        Long loginUserId = currentUserIdNullable();

        Set<Long> scopeVehicleIds = null;
        if (query.getMyRelation() != null && loginUserId != null) {
            if (Objects.equals(query.getMyRelation(), TwVehicleConstants.RELATION_OWNER)) {
                scopeVehicleIds = listOwnerVehicleIds(loginUserId);
            } else if (Objects.equals(query.getMyRelation(), TwVehicleConstants.RELATION_AUTH)) {
                scopeVehicleIds = listAuthVehicleIds(loginUserId);
            }
            if (scopeVehicleIds != null && scopeVehicleIds.isEmpty()) {
                return new Page<>(filter.getCurrent(), filter.getSize(), 0);
            }
        }
        if (query.getOwnerUserId() != null) {
            Set<Long> owned = listOwnerVehicleIds(query.getOwnerUserId());
            scopeVehicleIds = intersect(scopeVehicleIds, owned);
            if (scopeVehicleIds.isEmpty()) {
                return new Page<>(filter.getCurrent(), filter.getSize(), 0);
            }
        }
        if (query.getAuthUserId() != null) {
            Set<Long> authed = listAuthVehicleIds(query.getAuthUserId());
            scopeVehicleIds = intersect(scopeVehicleIds, authed);
            if (scopeVehicleIds.isEmpty()) {
                return new Page<>(filter.getCurrent(), filter.getSize(), 0);
            }
        }

        LambdaQueryWrapper<TwVehicle> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getVin())) {
            wrapper.likeRight(TwVehicle::getVin, query.getVin().trim().toUpperCase());
        }
        if (StrUtil.isNotBlank(query.getPlateNo())) {
            wrapper.like(TwVehicle::getPlateNo, query.getPlateNo().trim());
        }
        if (query.getStatus() != null) {
            wrapper.eq(TwVehicle::getStatus, query.getStatus());
        }
        if (scopeVehicleIds != null) {
            wrapper.in(TwVehicle::getId, scopeVehicleIds);
        }
        wrapper.orderByDesc(TwVehicle::getCreateTime);

        Page<TwVehicle> entityPage = page(new Page<>(filter.getCurrent(), filter.getSize()), wrapper);
        List<TwVehicle> records = entityPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            Page<TwVehiclePageVo> empty = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            empty.setRecords(List.of());
            return empty;
        }

        List<Long> vehicleIds = records.stream().map(TwVehicle::getId).toList();
        Map<Long, TwVehicleOwner> ownerMap = listActiveOwners(vehicleIds);
        Map<Long, Integer> authCountMap = countActiveAuths(vehicleIds);
        Map<String, Boolean> onlineMap = realtimeSupport.batchOnline(records.stream().map(TwVehicle::getVin).toList());

        Set<Long> userIds = ownerMap.values().stream().map(TwVehicleOwner::getOwnerUserId).collect(Collectors.toSet());
        Map<Long, ExtendSysUserSlimVo> userMap = loadUsers(userIds);

        Set<Long> myOwnerIds = loginUserId == null ? Set.of() : listOwnerVehicleIds(loginUserId);
        Set<Long> myAuthIds = loginUserId == null ? Set.of() : listAuthVehicleIds(loginUserId);

        List<TwVehiclePageVo> vos = new ArrayList<>();
        for (TwVehicle vehicle : records) {
            TwVehiclePageVo vo = new TwVehiclePageVo();
            BeanUtil.copyProperties(vehicle, vo);
            TwVehicleOwner owner = ownerMap.get(vehicle.getId());
            if (owner != null) {
                vo.setOwnerUserId(owner.getOwnerUserId());
                ExtendSysUserSlimVo slim = userMap.get(owner.getOwnerUserId());
                if (slim != null) {
                    vo.setOwnerUsername(slim.getUsername());
                }
            }
            vo.setAuthUserCount(authCountMap.getOrDefault(vehicle.getId(), 0));
            vo.setOnline(onlineMap.getOrDefault(vehicle.getVin(), false));
            if (myOwnerIds.contains(vehicle.getId())) {
                vo.setMyRelation(TwVehicleConstants.RELATION_OWNER);
            } else if (myAuthIds.contains(vehicle.getId())) {
                vo.setMyRelation(TwVehicleConstants.RELATION_AUTH);
            } else {
                vo.setMyRelation(TwVehicleConstants.RELATION_NONE);
            }
            vos.add(vo);
        }

        if (query.getOnlineStatus() != null) {
            boolean wantOnline = Objects.equals(query.getOnlineStatus(), 1);
            vos = vos.stream().filter(v -> wantOnline == Boolean.TRUE.equals(v.getOnline())).toList();
            Page<TwVehiclePageVo> filtered = new Page<>(entityPage.getCurrent(), entityPage.getSize(), vos.size());
            filtered.setRecords(vos);
            return filtered;
        }

        Page<TwVehiclePageVo> result = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        result.setRecords(vos);
        return result;
    }

    @Override
    public TwVehicleDetailVo detail(Long id) {
        TwVehicle vehicle = requireVehicle(id);
        TwVehicleDetailVo vo = new TwVehicleDetailVo();
        BeanUtil.copyProperties(vehicle, vo);

        TwVehicleOwner owner = ownerService.getActiveOwner(id);
        if (owner != null) {
            TwVehicleOwnerVo ownerVo = new TwVehicleOwnerVo();
            ownerVo.setUserId(owner.getOwnerUserId());
            ownerVo.setBindTime(owner.getBindTime());
            ExtendLoginInfoVo info = extendSysUserService.getUserInfoById(owner.getOwnerUserId());
            if (info != null) {
                ownerVo.setUsername(info.getUsername());
                ownerVo.setNickname(info.getUsername());
                ownerVo.setPhone(info.getPhone());
            }
            vo.setOwner(ownerVo);
        }

        vo.setAuthUsers(authService.listAuth(id, false));
        vo.setOnline(realtimeSupport.isOnline(vehicle.getVin()));
        vo.setLocation(realtimeSupport.latestLocation(vehicle.getVin()));
        vo.setDevice(loadDeviceSummary(vehicle.getVin()));

        Long loginUserId = currentUserIdNullable();
        if (loginUserId != null) {
            if (owner != null && Objects.equals(owner.getOwnerUserId(), loginUserId)) {
                vo.setMyRelation(TwVehicleConstants.RELATION_OWNER);
                vo.setMyAuthScope(TwVehicleConstants.SCOPE_ALL);
            } else {
                TwVehicleAuth auth = authService.getActiveAuth(id, loginUserId);
                if (auth != null) {
                    vo.setMyRelation(TwVehicleConstants.RELATION_AUTH);
                    vo.setMyAuthScope(auth.getAuthScope());
                } else {
                    vo.setMyRelation(TwVehicleConstants.RELATION_NONE);
                }
            }
        } else {
            vo.setMyRelation(TwVehicleConstants.RELATION_NONE);
        }
        return vo;
    }

    @Override
    public TwVehicle requireVehicle(Long id) {
        TwVehicle entity = getById(id);
        if (entity == null) {
            throw new BusinessException("车辆不存在");
        }
        return entity;
    }

    private TwVehicleDeviceSummaryVo loadDeviceSummary(String vin) {
        ExtendTwDeviceService deviceService = extendTwDeviceService.getIfAvailable();
        if (deviceService == null || StrUtil.isBlank(vin)) {
            return null;
        }
        try {
            ExtendTwDeviceSlimVo slim = deviceService.getByVin(vin);
            if (slim == null) {
                return null;
            }
            TwVehicleDeviceSummaryVo summary = new TwVehicleDeviceSummaryVo();
            summary.setDeviceId(slim.getDeviceId());
            summary.setStatus(slim.getStatus());
            return summary;
        } catch (Exception ex) {
            return null;
        }
    }

    private Long currentUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    private Long currentUserIdNullable() {
        try {
            if (!StpUtil.isLogin()) {
                return null;
            }
            return StpUtil.getLoginIdAsLong();
        } catch (Exception ex) {
            return null;
        }
    }

    private Set<Long> listOwnerVehicleIds(Long userId) {
        return ownerService.listActiveVehicleIdsByOwner(userId);
    }

    private Set<Long> listAuthVehicleIds(Long userId) {
        return authService.listActiveVehicleIdsByAuthUser(userId);
    }

    private Map<Long, TwVehicleOwner> listActiveOwners(List<Long> vehicleIds) {
        return ownerService.listActiveByVehicleIds(vehicleIds);
    }

    private Map<Long, Integer> countActiveAuths(List<Long> vehicleIds) {
        return authService.countActiveByVehicleIds(vehicleIds);
    }

    private Map<Long, ExtendSysUserSlimVo> loadUsers(Set<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Map.of();
        }
        IdsDto<Long> idsDto = new IdsDto<>();
        idsDto.setIds(new ArrayList<>(userIds));
        Map<Long, ExtendSysUserSlimVo> map = extendSysUserService.getUserSlimByIds(idsDto);
        return map == null ? Map.of() : map;
    }

    private static Set<Long> intersect(Set<Long> left, Set<Long> right) {
        if (left == null) {
            return new HashSet<>(right);
        }
        Set<Long> result = new HashSet<>(left);
        result.retainAll(right);
        return result;
    }
}
