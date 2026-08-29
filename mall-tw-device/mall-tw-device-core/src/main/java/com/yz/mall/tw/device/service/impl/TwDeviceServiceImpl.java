package com.yz.mall.tw.device.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.tw.device.constant.TwDeviceConstants;
import com.yz.mall.tw.device.dto.TwDeviceAddDto;
import com.yz.mall.tw.device.dto.TwDeviceBindDto;
import com.yz.mall.tw.device.dto.TwDeviceCredResetDto;
import com.yz.mall.tw.device.dto.TwDeviceQueryDto;
import com.yz.mall.tw.device.dto.TwDeviceStatusDto;
import com.yz.mall.tw.device.dto.TwDeviceUnbindDto;
import com.yz.mall.tw.device.dto.TwDeviceUpdateDto;
import com.yz.mall.tw.device.entity.TwDevice;
import com.yz.mall.tw.device.entity.TwDeviceVehicle;
import com.yz.mall.tw.device.mapper.TwDeviceMapper;
import com.yz.mall.tw.device.mapper.TwDeviceVehicleMapper;
import com.yz.mall.tw.device.service.TwDeviceService;
import com.yz.mall.tw.device.support.TwDeviceAccessNotifier;
import com.yz.mall.tw.device.vo.TwDeviceCreateVo;
import com.yz.mall.tw.device.vo.TwDeviceCredResetVo;
import com.yz.mall.tw.device.vo.TwDeviceDetailVo;
import com.yz.mall.tw.device.vo.TwDevicePageVo;
import com.yz.mall.tw.vehicle.service.ExtendTwVehicleService;
import com.yz.mall.tw.vehicle.vo.ExtendTwVehicleSlimVo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 终端管理服务实现
 */
@Service
public class TwDeviceServiceImpl extends ServiceImpl<TwDeviceMapper, TwDevice> implements TwDeviceService {

    private final TwDeviceVehicleMapper deviceVehicleMapper;
    private final PasswordEncoder passwordEncoder;
    private final ExtendTwVehicleService extendTwVehicleService;
    private final TwDeviceAccessNotifier accessNotifier;
    private final StringRedisTemplate stringRedisTemplate;

    public TwDeviceServiceImpl(TwDeviceVehicleMapper deviceVehicleMapper, PasswordEncoder passwordEncoder,
                               ExtendTwVehicleService extendTwVehicleService, TwDeviceAccessNotifier accessNotifier,
                               StringRedisTemplate stringRedisTemplate) {
        this.deviceVehicleMapper = deviceVehicleMapper;
        this.passwordEncoder = passwordEncoder;
        this.extendTwVehicleService = extendTwVehicleService;
        this.accessNotifier = accessNotifier;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TwDeviceCreateVo register(TwDeviceAddDto dto) {
        long id = IdUtil.getSnowflakeNextId();
        String deviceId = StrUtil.isBlank(dto.getDeviceId()) ? TwDeviceAccessNotifier.generateDeviceId(id) : dto.getDeviceId().trim();
        if (deviceId.length() > 64) {
            throw new BusinessException("终端号长度不能超过64");
        }
        if (count(new LambdaQueryWrapper<TwDevice>().eq(TwDevice::getDeviceId, deviceId)) > 0) {
            throw new BusinessException("终端号已注册");
        }
        String rawSecret = TwDeviceAccessNotifier.randomSecret();
        TwDevice device = new TwDevice();
        device.setId(id);
        device.setDeviceId(deviceId);
        device.setDeviceName(dto.getDeviceName());
        device.setDeviceType(StrUtil.blankToDefault(dto.getDeviceType(), TwDeviceConstants.DEFAULT_DEVICE_TYPE));
        device.setSecretHash(passwordEncoder.encode(rawSecret));
        device.setSecretAlgo(TwDeviceConstants.SECRET_ALGO_BCRYPT);
        device.setStatus(dto.getStatus() == null ? TwDeviceConstants.STATUS_ENABLED : dto.getStatus());
        device.setRemark(dto.getRemark());
        device.setCreateId(StpUtil.getLoginIdAsLong());
        device.setUpdateId(device.getCreateId());
        save(device);

        TwDeviceCreateVo vo = new TwDeviceCreateVo();
        vo.setId(id);
        vo.setDeviceId(deviceId);
        vo.setMqttUsername(deviceId);
        vo.setMqttPassword(rawSecret);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean edit(TwDeviceUpdateDto dto) {
        TwDevice device = requireDevice(dto.getId());
        if (dto.getDeviceName() != null) {
            device.setDeviceName(dto.getDeviceName());
        }
        if (dto.getDeviceType() != null) {
            device.setDeviceType(dto.getDeviceType());
        }
        if (dto.getRemark() != null) {
            device.setRemark(dto.getRemark());
        }
        if (dto.getFirmwareVersion() != null) {
            device.setFirmwareVersion(dto.getFirmwareVersion());
        }
        device.setUpdateId(StpUtil.getLoginIdAsLong());
        return updateById(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeStatus(TwDeviceStatusDto dto) {
        if (!Objects.equals(dto.getStatus(), TwDeviceConstants.STATUS_ENABLED) && !Objects.equals(dto.getStatus(), TwDeviceConstants.STATUS_DISABLED)) {
            throw new BusinessException("状态值非法");
        }
        TwDevice device = requireDevice(dto.getId());
        device.setStatus(dto.getStatus());
        device.setUpdateId(StpUtil.getLoginIdAsLong());
        boolean ok = updateById(device);
        if (ok && Objects.equals(dto.getStatus(), TwDeviceConstants.STATUS_DISABLED)) {
            accessNotifier.invalidateAndKick(device.getDeviceId(), "disable");
        }
        return ok;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDevice(Long id) {
        TwDevice device = requireDevice(id);
        if (getActiveBindByDevicePk(id) != null) {
            throw new BusinessException("请先解绑车辆");
        }
        boolean ok = lambdaUpdate().set(TwDevice::getInvalid, id).set(TwDevice::getUpdateId, StpUtil.getLoginIdAsLong())
                .eq(TwDevice::getId, id).eq(TwDevice::getInvalid, 0L).update();
        if (ok) {
            accessNotifier.invalidateAndKick(device.getDeviceId(), "delete");
        }
        return ok;
    }

    @Override
    public Page<TwDevicePageVo> pageDevices(PageFilter<TwDeviceQueryDto> filter) {
        TwDeviceQueryDto query = filter.getFilter() == null ? new TwDeviceQueryDto() : filter.getFilter();
        Set<Long> devicePkFilter = null;
        if (StrUtil.isNotBlank(query.getVin()) || query.getVehicleId() != null) {
            LambdaQueryWrapper<TwDeviceVehicle> bindQw = new LambdaQueryWrapper<TwDeviceVehicle>()
                    .eq(TwDeviceVehicle::getBindStatus, TwDeviceConstants.BIND_STATUS_ACTIVE);
            if (StrUtil.isNotBlank(query.getVin())) {
                bindQw.eq(TwDeviceVehicle::getVin, query.getVin().trim().toUpperCase());
            }
            if (query.getVehicleId() != null) {
                bindQw.eq(TwDeviceVehicle::getVehicleId, query.getVehicleId());
            }
            List<TwDeviceVehicle> binds = deviceVehicleMapper.selectList(bindQw);
            devicePkFilter = binds.stream().map(TwDeviceVehicle::getDevicePk).collect(Collectors.toSet());
            if (devicePkFilter.isEmpty()) {
                return new Page<>(filter.getCurrent(), filter.getSize(), 0);
            }
        }

        LambdaQueryWrapper<TwDevice> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getDeviceId())) {
            wrapper.likeRight(TwDevice::getDeviceId, query.getDeviceId().trim());
        }
        if (query.getStatus() != null) {
            wrapper.eq(TwDevice::getStatus, query.getStatus());
        }
        if (StrUtil.isNotBlank(query.getDeviceType())) {
            wrapper.eq(TwDevice::getDeviceType, query.getDeviceType());
        }
        if (devicePkFilter != null) {
            wrapper.in(TwDevice::getId, devicePkFilter);
        }
        wrapper.orderByDesc(TwDevice::getCreateTime);

        Page<TwDevice> entityPage = page(new Page<>(filter.getCurrent(), filter.getSize()), wrapper);
        List<TwDevice> records = entityPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            Page<TwDevicePageVo> empty = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
            empty.setRecords(List.of());
            return empty;
        }

        List<Long> pks = records.stream().map(TwDevice::getId).toList();
        List<TwDeviceVehicle> binds = deviceVehicleMapper.selectList(new LambdaQueryWrapper<TwDeviceVehicle>()
                .in(TwDeviceVehicle::getDevicePk, pks)
                .eq(TwDeviceVehicle::getBindStatus, TwDeviceConstants.BIND_STATUS_ACTIVE));
        Map<Long, TwDeviceVehicle> bindMap = binds.stream().collect(Collectors.toMap(TwDeviceVehicle::getDevicePk, b -> b, (a, b) -> a));

        List<TwDevicePageVo> vos = new ArrayList<>();
        for (TwDevice device : records) {
            TwDevicePageVo vo = new TwDevicePageVo();
            BeanUtil.copyProperties(device, vo);
            TwDeviceVehicle bind = bindMap.get(device.getId());
            if (bind != null) {
                vo.setVin(bind.getVin());
                vo.setVehicleId(bind.getVehicleId());
                vo.setOnline(isOnline(bind.getVin()));
                ExtendTwVehicleSlimVo vehicle = extendTwVehicleService.getByVin(bind.getVin());
                if (vehicle != null) {
                    vo.setPlateNo(vehicle.getPlateNo());
                }
            } else {
                vo.setOnline(false);
            }
            vos.add(vo);
        }

        if (query.getOnlineStatus() != null) {
            boolean wantOnline = Objects.equals(query.getOnlineStatus(), 1);
            vos = vos.stream().filter(v -> wantOnline == Boolean.TRUE.equals(v.getOnline())).toList();
            Page<TwDevicePageVo> filtered = new Page<>(entityPage.getCurrent(), entityPage.getSize(), vos.size());
            filtered.setRecords(vos);
            return filtered;
        }

        Page<TwDevicePageVo> result = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        result.setRecords(vos);
        return result;
    }

    @Override
    public TwDeviceDetailVo detail(Long id) {
        TwDevice device = requireDevice(id);
        TwDeviceDetailVo vo = new TwDeviceDetailVo();
        BeanUtil.copyProperties(device, vo);
        TwDeviceVehicle bind = getActiveBindByDevicePk(id);
        if (bind != null) {
            vo.setVehicleId(bind.getVehicleId());
            vo.setVin(bind.getVin());
            vo.setBindTime(bind.getBindTime());
            vo.setOnline(isOnline(bind.getVin()));
            ExtendTwVehicleSlimVo vehicle = extendTwVehicleService.getByVin(bind.getVin());
            if (vehicle != null) {
                vo.setPlateNo(vehicle.getPlateNo());
            }
        } else {
            vo.setOnline(false);
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long bind(TwDeviceBindDto dto) {
        TwDevice device = resolveDevice(dto.getId(), dto.getDeviceId());
        if (!Objects.equals(device.getStatus(), TwDeviceConstants.STATUS_ENABLED)) {
            throw new BusinessException("终端已禁用，无法绑定");
        }
        if (getActiveBindByDevicePk(device.getId()) != null) {
            throw new BusinessException("请先解绑当前车辆");
        }

        ExtendTwVehicleSlimVo vehicle = resolveVehicle(dto.getVehicleId(), dto.getVin());
        if (getActiveBindByVehicleId(vehicle.getId()) != null) {
            throw new BusinessException("该车辆已绑定终端（一车一终端）");
        }

        TwDeviceVehicle bind = new TwDeviceVehicle();
        bind.setId(IdUtil.getSnowflakeNextId());
        bind.setDevicePk(device.getId());
        bind.setDeviceId(device.getDeviceId());
        bind.setVehicleId(vehicle.getId());
        bind.setVin(vehicle.getVin());
        bind.setBindStatus(TwDeviceConstants.BIND_STATUS_ACTIVE);
        bind.setBindTime(LocalDateTime.now());
        bind.setRemark(dto.getRemark());
        bind.setCreateId(StpUtil.getLoginIdAsLong());
        bind.setUpdateId(bind.getCreateId());
        deviceVehicleMapper.insert(bind);
        accessNotifier.invalidateAndKick(device.getDeviceId(), "bind");
        return bind.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unbind(TwDeviceUnbindDto dto) {
        TwDeviceVehicle bind = resolveActiveBind(dto.getId(), dto.getDeviceId(), dto.getVehicleId(), dto.getVin());
        if (bind == null) {
            throw new BusinessException("当前无有效绑定");
        }
        bind.setBindStatus(TwDeviceConstants.BIND_STATUS_UNBOUND);
        bind.setUnbindTime(LocalDateTime.now());
        bind.setRemark(dto.getRemark());
        bind.setUpdateId(StpUtil.getLoginIdAsLong());
        int rows = deviceVehicleMapper.updateById(bind);
        accessNotifier.invalidateAndKick(bind.getDeviceId(), "unbind");
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TwDeviceCredResetVo resetCredential(TwDeviceCredResetDto dto) {
        TwDevice device = resolveDevice(dto.getId(), dto.getDeviceId());
        String rawSecret = TwDeviceAccessNotifier.randomSecret();
        LocalDateTime now = LocalDateTime.now();
        device.setSecretHash(passwordEncoder.encode(rawSecret));
        device.setSecretAlgo(TwDeviceConstants.SECRET_ALGO_BCRYPT);
        device.setLastCredResetTime(now);
        device.setUpdateId(StpUtil.getLoginIdAsLong());
        updateById(device);
        accessNotifier.invalidateAndKick(device.getDeviceId(), "cred-reset");

        TwDeviceCredResetVo vo = new TwDeviceCredResetVo();
        vo.setDeviceId(device.getDeviceId());
        vo.setMqttPassword(rawSecret);
        vo.setResetTime(now);
        return vo;
    }

    @Override
    public TwDevice requireDevice(Long id) {
        TwDevice device = getById(id);
        if (device == null) {
            throw new BusinessException("终端不存在");
        }
        return device;
    }

    @Override
    public TwDevice requireByDeviceId(String deviceId) {
        TwDevice device = getOne(new LambdaQueryWrapper<TwDevice>().eq(TwDevice::getDeviceId, deviceId).last("limit 1"), false);
        if (device == null) {
            throw new BusinessException("终端不存在");
        }
        return device;
    }

    @Override
    public TwDeviceVehicle getActiveBindByDevicePk(Long devicePk) {
        return deviceVehicleMapper.selectOne(new LambdaQueryWrapper<TwDeviceVehicle>()
                .eq(TwDeviceVehicle::getDevicePk, devicePk)
                .eq(TwDeviceVehicle::getBindStatus, TwDeviceConstants.BIND_STATUS_ACTIVE)
                .last("limit 1"));
    }

    @Override
    public TwDeviceVehicle getActiveBindByVehicleId(Long vehicleId) {
        return deviceVehicleMapper.selectOne(new LambdaQueryWrapper<TwDeviceVehicle>()
                .eq(TwDeviceVehicle::getVehicleId, vehicleId)
                .eq(TwDeviceVehicle::getBindStatus, TwDeviceConstants.BIND_STATUS_ACTIVE)
                .last("limit 1"));
    }

    @Override
    public TwDeviceVehicle getActiveBindByVin(String vin) {
        return deviceVehicleMapper.selectOne(new LambdaQueryWrapper<TwDeviceVehicle>()
                .eq(TwDeviceVehicle::getVin, vin.trim().toUpperCase())
                .eq(TwDeviceVehicle::getBindStatus, TwDeviceConstants.BIND_STATUS_ACTIVE)
                .last("limit 1"));
    }

    private TwDevice resolveDevice(Long id, String deviceId) {
        if (id != null) {
            return requireDevice(id);
        }
        if (StrUtil.isNotBlank(deviceId)) {
            return requireByDeviceId(deviceId.trim());
        }
        throw new BusinessException("请指定终端 id 或 deviceId");
    }

    private ExtendTwVehicleSlimVo resolveVehicle(Long vehicleId, String vin) {
        if (vehicleId != null) {
            ExtendTwVehicleSlimVo byId = extendTwVehicleService.getById(vehicleId);
            if (byId == null || !Objects.equals(byId.getStatus(), TwDeviceConstants.STATUS_ENABLED)) {
                throw new BusinessException("车辆不存在或未启用");
            }
            if (StrUtil.isNotBlank(vin) && !Objects.equals(byId.getVin(), vin.trim().toUpperCase())) {
                throw new BusinessException("vehicleId 与 vin 不匹配");
            }
            return byId;
        }
        if (StrUtil.isNotBlank(vin)) {
            ExtendTwVehicleSlimVo byVin = extendTwVehicleService.getByVin(vin.trim().toUpperCase());
            if (byVin == null || !Objects.equals(byVin.getStatus(), TwDeviceConstants.STATUS_ENABLED)) {
                throw new BusinessException("车辆不存在或未启用");
            }
            return byVin;
        }
        throw new BusinessException("请指定 vehicleId 或 vin");
    }

    private TwDeviceVehicle resolveActiveBind(Long id, String deviceId, Long vehicleId, String vin) {
        if (id != null) {
            TwDevice device = requireDevice(id);
            return getActiveBindByDevicePk(device.getId());
        }
        if (StrUtil.isNotBlank(deviceId)) {
            TwDevice device = requireByDeviceId(deviceId.trim());
            return getActiveBindByDevicePk(device.getId());
        }
        if (vehicleId != null) {
            return getActiveBindByVehicleId(vehicleId);
        }
        if (StrUtil.isNotBlank(vin)) {
            return getActiveBindByVin(vin);
        }
        throw new BusinessException("请指定解绑定位条件");
    }

    private boolean isOnline(String vin) {
        if (StrUtil.isBlank(vin)) {
            return false;
        }
        String raw = stringRedisTemplate.opsForValue().get(TwDeviceConstants.REDIS_ONLINE_VIN_PREFIX + vin);
        if (StrUtil.isBlank(raw)) {
            return false;
        }
        String v = raw.trim().toLowerCase();
        return !List.of("0", "false", "offline", "null").contains(v);
    }
}
