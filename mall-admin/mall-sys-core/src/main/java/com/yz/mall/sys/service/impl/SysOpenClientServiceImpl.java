package com.yz.mall.sys.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.enums.EnableEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.base.exception.DataNotExistException;
import com.yz.mall.sys.SysOpenCryptoProperties;
import com.yz.mall.sys.dto.*;
import com.yz.mall.sys.entity.SysOpenClient;
import com.yz.mall.sys.entity.SysOpenClientAuth;
import com.yz.mall.sys.entity.SysOpenClientKey;
import com.yz.mall.sys.mapper.SysOpenClientAuthMapper;
import com.yz.mall.sys.mapper.SysOpenClientKeyMapper;
import com.yz.mall.sys.mapper.SysOpenClientMapper;
import com.yz.mall.sys.service.SysOpenClientService;
import com.yz.mall.sys.vo.SysOpenClientCreateVo;
import com.yz.mall.sys.vo.SysOpenClientDetailVo;
import com.yz.mall.sys.vo.SysOpenClientKeySummaryVo;
import com.yz.mall.sys.vo.SysOpenPermissionOptionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 第三方开放客户端(SysOpenClient)表服务实现类
 *
 * @author yunze
 */
@Service
public class SysOpenClientServiceImpl extends ServiceImpl<SysOpenClientMapper, SysOpenClient> implements SysOpenClientService {

    private static final List<SysOpenPermissionOptionVo> PERMISSION_OPTIONS = List.of(
            new SysOpenPermissionOptionVo("open:tw:vehicle:query", "车辆档案查询"),
            new SysOpenPermissionOptionVo("open:tw:telemetry:latest", "最新位置查询"),
            new SysOpenPermissionOptionVo("open:tw:telemetry:track", "轨迹查询"),
            new SysOpenPermissionOptionVo("open:tw:command:send", "远程指令")
    );

    private final SysOpenClientKeyMapper keyMapper;
    private final SysOpenClientAuthMapper authMapper;
    private final SysOpenCryptoProperties cryptoProperties;

    public SysOpenClientServiceImpl(SysOpenClientKeyMapper keyMapper, SysOpenClientAuthMapper authMapper,
                                   SysOpenCryptoProperties cryptoProperties) {
        this.keyMapper = keyMapper;
        this.authMapper = authMapper;
        this.cryptoProperties = cryptoProperties;
    }

    @Override
    public SysOpenClientCreateVo save(SysOpenClientAddDto dto) {
        SysOpenClient bo = new SysOpenClient();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setClientId(IdUtil.fastSimpleUUID());
        bo.setStatus(EnableEnum.ENABLE.get());
        bo.setCreateId(StpUtil.getLoginIdAsLong());
        bo.setUpdateId(bo.getCreateId());
        baseMapper.insert(bo);
        return new SysOpenClientCreateVo(bo.getId(), bo.getClientId());
    }

    @Override
    public boolean update(SysOpenClientUpdateDto dto) {
        SysOpenClient existing = baseMapper.selectById(dto.getId());
        if (existing == null) {
            throw new DataNotExistException("客户端不存在");
        }
        SysOpenClient bo = new SysOpenClient();
        BeanUtils.copyProperties(dto, bo);
        bo.setClientId(null);
        bo.setUpdateId(StpUtil.getLoginIdAsLong());
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public boolean switchStatus(Long id) {
        SysOpenClient client = baseMapper.selectById(id);
        if (client == null) {
            throw new DataNotExistException("客户端不存在");
        }
        client.setStatus(Objects.equals(EnableEnum.ENABLE.get(), client.getStatus())
                ? EnableEnum.Disable.get() : EnableEnum.ENABLE.get());
        client.setUpdateId(StpUtil.getLoginIdAsLong());
        return baseMapper.updateById(client) > 0;
    }

    @DS("slave")
    @Override
    public Page<SysOpenClient> page(PageFilter<SysOpenClientQueryDto> filter) {
        LambdaQueryWrapper<SysOpenClient> queryWrapper = new LambdaQueryWrapper<>();
        SysOpenClientQueryDto f = filter.getFilter();
        if (f != null) {
            queryWrapper.eq(StringUtils.hasText(f.getClientId()), SysOpenClient::getClientId, f.getClientId());
            queryWrapper.like(StringUtils.hasText(f.getClientName()), SysOpenClient::getClientName, f.getClientName());
            queryWrapper.eq(f.getStatus() != null, SysOpenClient::getStatus, f.getStatus());
        }
        queryWrapper.orderByDesc(SysOpenClient::getCreateTime);
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @DS("slave")
    @Override
    public SysOpenClientDetailVo detail(Long id) {
        SysOpenClient client = baseMapper.selectById(id);
        if (client == null) {
            throw new DataNotExistException("客户端不存在");
        }
        SysOpenClientDetailVo vo = new SysOpenClientDetailVo();
        BeanUtils.copyProperties(client, vo);
        SysOpenClientKey currentKey = getCurrentKey(client.getClientId());
        if (currentKey != null) {
            vo.setCurrentKey(toKeySummary(currentKey));
        }
        vo.setAuthList(listAuth(client.getClientId()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeClient(Long id) {
        SysOpenClient client = baseMapper.selectById(id);
        if (client == null) {
            throw new DataNotExistException("客户端不存在");
        }
        LambdaUpdateWrapper<SysOpenClientKey> keyUpdate = new LambdaUpdateWrapper<>();
        keyUpdate.eq(SysOpenClientKey::getClientId, client.getClientId());
        keyMapper.delete(keyUpdate);
        LambdaUpdateWrapper<SysOpenClientAuth> authUpdate = new LambdaUpdateWrapper<>();
        authUpdate.eq(SysOpenClientAuth::getClientId, client.getClientId());
        authMapper.delete(authUpdate);
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uploadKey(SysOpenClientKeyUploadDto dto) {
        SysOpenClient client = getClientByClientId(dto.getClientId());
        if (client == null) {
            throw new DataNotExistException("客户端不存在");
        }
        Long loginId = StpUtil.getLoginIdAsLong();
        LambdaUpdateWrapper<SysOpenClientKey> stopOld = new LambdaUpdateWrapper<>();
        stopOld.eq(SysOpenClientKey::getClientId, dto.getClientId())
                .eq(SysOpenClientKey::getKeyStatus, EnableEnum.ENABLE.get())
                .set(SysOpenClientKey::getKeyStatus, EnableEnum.Disable.get())
                .set(SysOpenClientKey::getExpireTime, LocalDateTime.now())
                .set(SysOpenClientKey::getUpdateId, loginId);
        keyMapper.update(null, stopOld);
        LambdaQueryWrapper<SysOpenClientKey> maxVersionQuery = new LambdaQueryWrapper<>();
        maxVersionQuery.eq(SysOpenClientKey::getClientId, dto.getClientId())
                .orderByDesc(SysOpenClientKey::getKeyVersion)
                .last("LIMIT 1");
        SysOpenClientKey latest = keyMapper.selectOne(maxVersionQuery);
        int newVersion = latest == null ? 1 : latest.getKeyVersion() + 1;
        SysOpenClientKey newKey = new SysOpenClientKey();
        newKey.setId(IdUtil.getSnowflakeNextId());
        newKey.setClientId(dto.getClientId());
        newKey.setKeyVersion(newVersion);
        newKey.setClientPublicKey(dto.getClientPublicKey());
        newKey.setKeyStatus(EnableEnum.ENABLE.get());
        newKey.setEffectTime(LocalDateTime.now());
        newKey.setRemark(dto.getRemark());
        newKey.setCreateId(loginId);
        newKey.setUpdateId(loginId);
        return keyMapper.insert(newKey) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> generateKey(String clientId) {
        SysOpenClient client = getClientByClientId(clientId);
        if (client == null) {
            throw new DataNotExistException("客户端不存在");
        }
        try {
            SM2 sm2 = SmUtil.sm2();
            String publicKeyBase64 = sm2.getPublicKeyBase64();
            String privateKeyBase64 = sm2.getPrivateKeyBase64();
            SysOpenClientKeyUploadDto uploadDto = new SysOpenClientKeyUploadDto();
            uploadDto.setClientId(clientId);
            uploadDto.setClientPublicKey(publicKeyBase64);
            uploadDto.setRemark("平台代生成密钥对");
            uploadKey(uploadDto);
            Map<String, String> result = new HashMap<>();
            result.put("clientId", clientId);
            result.put("publicKey", publicKeyBase64);
            result.put("privateKey", privateKeyBase64);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("生成SM2密钥对失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean grantAuth(SysOpenClientAuthDto dto) {
        SysOpenClient client = getClientByClientId(dto.getClientId());
        if (client == null) {
            throw new DataNotExistException("客户端不存在");
        }
        Long loginId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<SysOpenClientAuth> existQuery = new LambdaQueryWrapper<>();
        existQuery.eq(SysOpenClientAuth::getClientId, dto.getClientId());
        List<SysOpenClientAuth> existing = authMapper.selectList(existQuery);
        Map<String, SysOpenClientAuth> existMap = existing.stream()
                .collect(Collectors.toMap(SysOpenClientAuth::getPermissionCode, a -> a, (a, b) -> a));
        List<SysOpenClientAuth> toInsert = new ArrayList<>();
        for (String code : dto.getPermissionCodes()) {
            SysOpenClientAuth exist = existMap.get(code);
            if (exist != null) {
                if (Objects.equals(exist.getAuthStatus(), EnableEnum.Disable.get())) {
                    exist.setAuthStatus(EnableEnum.ENABLE.get());
                    exist.setGrantTime(LocalDateTime.now());
                    exist.setUpdateId(loginId);
                    authMapper.updateById(exist);
                }
            } else {
                SysOpenClientAuth auth = new SysOpenClientAuth();
                auth.setId(IdUtil.getSnowflakeNextId());
                auth.setClientId(dto.getClientId());
                auth.setPermissionCode(code);
                auth.setAuthStatus(EnableEnum.ENABLE.get());
                auth.setGrantTime(LocalDateTime.now());
                auth.setRemark(dto.getRemark());
                auth.setCreateId(loginId);
                auth.setUpdateId(loginId);
                toInsert.add(auth);
            }
        }
        for (SysOpenClientAuth auth : toInsert) {
            authMapper.insert(auth);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeAuth(SysOpenClientAuthDto dto) {
        Long loginId = StpUtil.getLoginIdAsLong();
        LambdaUpdateWrapper<SysOpenClientAuth> update = new LambdaUpdateWrapper<>();
        update.eq(SysOpenClientAuth::getClientId, dto.getClientId())
                .in(SysOpenClientAuth::getPermissionCode, dto.getPermissionCodes())
                .set(SysOpenClientAuth::getAuthStatus, EnableEnum.Disable.get())
                .set(SysOpenClientAuth::getUpdateId, loginId);
        return authMapper.update(null, update) > 0;
    }

    @DS("slave")
    @Override
    public List<SysOpenClientAuth> listAuth(String clientId) {
        LambdaQueryWrapper<SysOpenClientAuth> query = new LambdaQueryWrapper<>();
        query.eq(SysOpenClientAuth::getClientId, clientId)
                .eq(SysOpenClientAuth::getAuthStatus, EnableEnum.ENABLE.get())
                .orderByDesc(SysOpenClientAuth::getGrantTime);
        return authMapper.selectList(query);
    }

    @DS("slave")
    @Override
    public SysOpenClientKey getCurrentKey(String clientId) {
        LambdaQueryWrapper<SysOpenClientKey> query = new LambdaQueryWrapper<>();
        query.eq(SysOpenClientKey::getClientId, clientId)
                .eq(SysOpenClientKey::getKeyStatus, EnableEnum.ENABLE.get())
                .last("LIMIT 1");
        return keyMapper.selectOne(query);
    }

    @Override
    public List<SysOpenPermissionOptionVo> listPermissionOptions() {
        return PERMISSION_OPTIONS;
    }

    @Override
    public String getServerPublicKey() {
        String key = cryptoProperties.getServerPublicKey();
        if (!StringUtils.hasText(key)) {
            throw new BusinessException("平台服务端公钥未配置，请设置环境变量 SYS_OPEN_SERVER_PUBLIC_KEY 或配置 yz.mall.sys.open.server-public-key");
        }
        return key;
    }

    /**
     * 根据 clientId 查询客户端
     */
    private SysOpenClient getClientByClientId(String clientId) {
        if (!StringUtils.hasText(clientId)) {
            return null;
        }
        LambdaQueryWrapper<SysOpenClient> query = new LambdaQueryWrapper<>();
        query.eq(SysOpenClient::getClientId, clientId);
        return baseMapper.selectOne(query);
    }

    /**
     * 公钥记录转摘要视图
     */
    private SysOpenClientKeySummaryVo toKeySummary(SysOpenClientKey key) {
        SysOpenClientKeySummaryVo summary = new SysOpenClientKeySummaryVo();
        summary.setId(key.getId());
        summary.setKeyVersion(key.getKeyVersion());
        summary.setEffectTime(key.getEffectTime());
        summary.setRemark(key.getRemark());
        String pub = key.getClientPublicKey();
        if (StringUtils.hasText(pub)) {
            summary.setFingerprint(SmUtil.sm3(pub));
            summary.setPublicKeyPreview(pub.length() > 32 ? pub.substring(0, 32) + "..." : pub);
        }
        return summary;
    }
}
