package com.yz.mall.tw.vehicle.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.IdsDto;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.sys.service.ExtendSysUserService;
import com.yz.mall.sys.vo.ExtendLoginInfoVo;
import com.yz.mall.sys.vo.ExtendSysUserSlimVo;
import com.yz.mall.tw.vehicle.config.TwVehicleProperties;
import com.yz.mall.tw.vehicle.constant.TwVehicleConstants;
import com.yz.mall.tw.vehicle.dto.TwVehicleAuthGrantDto;
import com.yz.mall.tw.vehicle.dto.TwVehicleAuthRevokeDto;
import com.yz.mall.tw.vehicle.entity.TwVehicle;
import com.yz.mall.tw.vehicle.entity.TwVehicleAuth;
import com.yz.mall.tw.vehicle.entity.TwVehicleOwner;
import com.yz.mall.tw.vehicle.mapper.TwVehicleAuthMapper;
import com.yz.mall.tw.vehicle.service.TwVehicleAuthService;
import com.yz.mall.tw.vehicle.service.TwVehicleOwnerService;
import com.yz.mall.tw.vehicle.service.TwVehicleService;
import com.yz.mall.tw.vehicle.vo.TwVehicleAuthVo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 车辆授权服务实现
 */
@Service
public class TwVehicleAuthServiceImpl extends ServiceImpl<TwVehicleAuthMapper, TwVehicleAuth> implements TwVehicleAuthService {

    private final TwVehicleService vehicleService;
    private final TwVehicleOwnerService ownerService;
    private final ExtendSysUserService extendSysUserService;
    private final TwVehicleProperties properties;

    public TwVehicleAuthServiceImpl(@Lazy TwVehicleService vehicleService, @Lazy TwVehicleOwnerService ownerService,
                                    ExtendSysUserService extendSysUserService, TwVehicleProperties properties) {
        this.vehicleService = vehicleService;
        this.ownerService = ownerService;
        this.extendSysUserService = extendSysUserService;
        this.properties = properties;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long grant(TwVehicleAuthGrantDto dto) {
        TwVehicle vehicle = vehicleService.requireVehicle(dto.getVehicleId());
        if (!Objects.equals(vehicle.getStatus(), TwVehicleConstants.STATUS_ENABLED)) {
            throw new BusinessException("车辆已停用，无法授权");
        }
        TwVehicleOwner owner = ownerService.getActiveOwner(dto.getVehicleId());
        if (owner == null) {
            throw new BusinessException("车辆尚无车主，无法授权");
        }
        Long operatorId = StpUtil.getLoginIdAsLong();
        boolean isOwner = Objects.equals(owner.getOwnerUserId(), operatorId);
        boolean isOperator = StpUtil.hasPermission("api:tw:vehicle:owner:bind");
        if (!isOwner && !isOperator) {
            throw new BusinessException("仅车主可授权");
        }
        if (Objects.equals(dto.getAuthUserId(), owner.getOwnerUserId())) {
            throw new BusinessException("不能授权给当前车主");
        }
        ExtendLoginInfoVo authUser = extendSysUserService.getUserInfoById(dto.getAuthUserId());
        if (authUser == null || authUser.getId() == null) {
            throw new BusinessException("用户不存在");
        }

        TwVehicleAuth active = getActiveAuth(dto.getVehicleId(), dto.getAuthUserId());
        int scope = dto.getAuthScope() == null ? TwVehicleConstants.SCOPE_DEFAULT : dto.getAuthScope();
        if (active != null) {
            active.setAuthScope(scope);
            active.setExpireTime(dto.getExpireTime());
            active.setRemark(dto.getRemark());
            active.setUpdateId(operatorId);
            updateById(active);
            return active.getId();
        }
        if (countActive(dto.getVehicleId()) >= properties.getMaxAuthUsers()) {
            throw new BusinessException("授权用户数已达上限");
        }

        TwVehicleAuth auth = new TwVehicleAuth();
        auth.setId(IdUtil.getSnowflakeNextId());
        auth.setVehicleId(vehicle.getId());
        auth.setVin(vehicle.getVin());
        auth.setOwnerUserId(owner.getOwnerUserId());
        auth.setAuthUserId(dto.getAuthUserId());
        auth.setAuthScope(scope);
        auth.setAuthStatus(TwVehicleConstants.AUTH_STATUS_ACTIVE);
        auth.setGrantTime(LocalDateTime.now());
        auth.setExpireTime(dto.getExpireTime());
        auth.setRemark(dto.getRemark());
        auth.setCreateId(operatorId);
        auth.setUpdateId(operatorId);
        save(auth);
        return auth.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean revoke(TwVehicleAuthRevokeDto dto) {
        TwVehicleAuth auth;
        if (dto.getAuthId() != null) {
            auth = getById(dto.getAuthId());
        } else if (dto.getAuthUserId() != null) {
            auth = getActiveAuth(dto.getVehicleId(), dto.getAuthUserId());
        } else {
            throw new BusinessException("请指定授权记录或被授权用户");
        }
        if (auth == null || !Objects.equals(auth.getVehicleId(), dto.getVehicleId())) {
            throw new BusinessException("授权记录不存在或已撤销");
        }
        if (!Objects.equals(auth.getAuthStatus(), TwVehicleConstants.AUTH_STATUS_ACTIVE)) {
            throw new BusinessException("授权记录不存在或已撤销");
        }

        Long operatorId = StpUtil.getLoginIdAsLong();
        TwVehicleOwner owner = ownerService.getActiveOwner(dto.getVehicleId());
        boolean isOwner = owner != null && Objects.equals(owner.getOwnerUserId(), operatorId);
        boolean isSelf = Objects.equals(auth.getAuthUserId(), operatorId);
        boolean isOperator = StpUtil.hasPermission("api:tw:vehicle:owner:bind");
        if (!isOwner && !isSelf && !isOperator) {
            throw new BusinessException("无权撤销该授权");
        }

        auth.setAuthStatus(TwVehicleConstants.AUTH_STATUS_REVOKED);
        auth.setRevokeTime(LocalDateTime.now());
        auth.setRemark(dto.getRemark());
        auth.setUpdateId(operatorId);
        return updateById(auth);
    }

    @Override
    public List<TwVehicleAuthVo> listAuth(Long vehicleId, boolean includeHistory) {
        vehicleService.requireVehicle(vehicleId);
        LambdaQueryWrapper<TwVehicleAuth> wrapper = new LambdaQueryWrapper<TwVehicleAuth>().eq(TwVehicleAuth::getVehicleId, vehicleId);
        if (!includeHistory) {
            wrapper.eq(TwVehicleAuth::getAuthStatus, TwVehicleConstants.AUTH_STATUS_ACTIVE)
                    .and(w -> w.isNull(TwVehicleAuth::getExpireTime).or().gt(TwVehicleAuth::getExpireTime, LocalDateTime.now()));
        }
        wrapper.orderByDesc(TwVehicleAuth::getGrantTime);
        List<TwVehicleAuth> list = list(wrapper);
        if (CollUtil.isEmpty(list)) {
            return List.of();
        }
        Set<Long> userIds = list.stream().map(TwVehicleAuth::getAuthUserId).collect(Collectors.toSet());
        IdsDto<Long> idsDto = new IdsDto<>();
        idsDto.setIds(new ArrayList<>(userIds));
        Map<Long, ExtendSysUserSlimVo> userMap = extendSysUserService.getUserSlimByIds(idsDto);
        if (userMap == null) {
            userMap = Map.of();
        }
        List<TwVehicleAuthVo> result = new ArrayList<>();
        for (TwVehicleAuth auth : list) {
            TwVehicleAuthVo vo = new TwVehicleAuthVo();
            vo.setId(auth.getId());
            vo.setAuthUserId(auth.getAuthUserId());
            vo.setAuthScope(auth.getAuthScope());
            vo.setAuthStatus(auth.getAuthStatus());
            vo.setGrantTime(auth.getGrantTime());
            vo.setExpireTime(auth.getExpireTime());
            vo.setRevokeTime(auth.getRevokeTime());
            ExtendSysUserSlimVo slim = userMap.get(auth.getAuthUserId());
            if (slim != null) {
                vo.setUsername(slim.getUsername());
                vo.setNickname(slim.getUsername());
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeAllActive(Long vehicleId, Long operatorId) {
        List<TwVehicleAuth> list = list(new LambdaQueryWrapper<TwVehicleAuth>()
                .eq(TwVehicleAuth::getVehicleId, vehicleId)
                .eq(TwVehicleAuth::getAuthStatus, TwVehicleConstants.AUTH_STATUS_ACTIVE));
        LocalDateTime now = LocalDateTime.now();
        for (TwVehicleAuth auth : list) {
            auth.setAuthStatus(TwVehicleConstants.AUTH_STATUS_REVOKED);
            auth.setRevokeTime(now);
            auth.setUpdateId(operatorId);
        }
        if (CollUtil.isNotEmpty(list)) {
            updateBatchById(list);
        }
    }

    @Override
    public TwVehicleAuth getActiveAuth(Long vehicleId, Long userId) {
        TwVehicleAuth auth = getOne(new LambdaQueryWrapper<TwVehicleAuth>()
                .eq(TwVehicleAuth::getVehicleId, vehicleId)
                .eq(TwVehicleAuth::getAuthUserId, userId)
                .eq(TwVehicleAuth::getAuthStatus, TwVehicleConstants.AUTH_STATUS_ACTIVE)
                .last("limit 1"), false);
        if (auth == null) {
            return null;
        }
        if (auth.getExpireTime() != null && auth.getExpireTime().isBefore(LocalDateTime.now())) {
            return null;
        }
        return auth;
    }

    @Override
    public int countActive(Long vehicleId) {
        return (int) count(new LambdaQueryWrapper<TwVehicleAuth>()
                .eq(TwVehicleAuth::getVehicleId, vehicleId)
                .eq(TwVehicleAuth::getAuthStatus, TwVehicleConstants.AUTH_STATUS_ACTIVE)
                .and(w -> w.isNull(TwVehicleAuth::getExpireTime).or().gt(TwVehicleAuth::getExpireTime, LocalDateTime.now())));
    }

    /**
     * 被授权用户可见车辆 ID
     */
    @Override
    public Set<Long> listActiveVehicleIdsByAuthUser(Long authUserId) {
        List<TwVehicleAuth> list = list(new LambdaQueryWrapper<TwVehicleAuth>()
                .eq(TwVehicleAuth::getAuthUserId, authUserId)
                .eq(TwVehicleAuth::getAuthStatus, TwVehicleConstants.AUTH_STATUS_ACTIVE)
                .and(w -> w.isNull(TwVehicleAuth::getExpireTime).or().gt(TwVehicleAuth::getExpireTime, LocalDateTime.now()))
                .select(TwVehicleAuth::getVehicleId));
        return list.stream().map(TwVehicleAuth::getVehicleId).collect(Collectors.toSet());
    }

    /**
     * 批量统计有效授权人数
     */
    @Override
    public Map<Long, Integer> countActiveByVehicleIds(List<Long> vehicleIds) {
        if (vehicleIds == null || vehicleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<TwVehicleAuth> list = list(new LambdaQueryWrapper<TwVehicleAuth>()
                .in(TwVehicleAuth::getVehicleId, vehicleIds)
                .eq(TwVehicleAuth::getAuthStatus, TwVehicleConstants.AUTH_STATUS_ACTIVE)
                .and(w -> w.isNull(TwVehicleAuth::getExpireTime).or().gt(TwVehicleAuth::getExpireTime, LocalDateTime.now()))
                .select(TwVehicleAuth::getVehicleId));
        Map<Long, Integer> map = new HashMap<>();
        for (TwVehicleAuth auth : list) {
            map.merge(auth.getVehicleId(), 1, Integer::sum);
        }
        return map;
    }
}
