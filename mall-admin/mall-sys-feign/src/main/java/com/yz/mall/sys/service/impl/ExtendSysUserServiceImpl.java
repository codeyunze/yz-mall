package com.yz.mall.sys.service.impl;

import com.yz.mall.base.IdsDto;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.FeignException;
import com.yz.mall.sys.dto.ExtendSysUserAddDto;
import com.yz.mall.sys.dto.ExtendSysUserBalanceDto;
import com.yz.mall.sys.feign.ExtendSysUserFeign;
import com.yz.mall.sys.service.ExtendSysUserService;
import com.yz.mall.sys.vo.ExtendLoginInfoVo;
import com.yz.mall.sys.vo.ExtendSysUserSlimVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 内部暴露service实现类: 用户信息
 *
 * @author yunze
 * @date 2024/6/22 23:42
 */
@Service
public class ExtendSysUserServiceImpl implements ExtendSysUserService {

    private final ExtendSysUserFeign feign;

    public ExtendSysUserServiceImpl(ExtendSysUserFeign feign) {
        this.feign = feign;
    }

    @Override
    public void deduct(Long userId, BigDecimal amount) {
        Result<Boolean> result = feign.deduct(new ExtendSysUserBalanceDto(userId, amount));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
    }

    @Override
    public void recharge(Long userId, BigDecimal amount) {
        Result<Boolean> result = feign.deduct(new ExtendSysUserBalanceDto(userId, amount));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        Result<List<Long>> result = feign.getUserRoles(userId);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

    @Override
    public ExtendLoginInfoVo getUserInfoById(Long userId) {
        Result<ExtendLoginInfoVo> result = feign.getUserInfo(userId);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

    @Override
    public Long add(ExtendSysUserAddDto dto) {
        Result<Long> result = feign.add(dto);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

    @Override
    public Map<Long, ExtendSysUserSlimVo> getUserSlimByIds(IdsDto<Long> idsDto) {
        Result<Map<Long, ExtendSysUserSlimVo>> result = feign.getUserSlimByIds(idsDto);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        if (CollectionUtils.isEmpty(result.getData())) {
            return Map.of();
        }
        return result.getData();
    }
}
