package com.yz.mall.sys.service.impl;

import com.yz.mall.web.exception.BusinessException;
import com.yz.mall.web.exception.FeignException;
import com.yz.mall.sys.dto.InternalLoginInfoDto;
import com.yz.mall.sys.dto.InternalSysUserAddDto;
import com.yz.mall.sys.dto.InternalSysUserBalanceDto;
import com.yz.mall.sys.dto.InternalSysUserCheckLoginDto;
import com.yz.mall.sys.feign.InternalSysUserFeign;
import com.yz.mall.sys.service.InternalSysUserService;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.enums.CodeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 内部暴露service实现类: 用户信息
 *
 * @author yunze
 * @date 2024/6/22 23:42
 */
@Service
public class InternalSysUserServiceImpl implements InternalSysUserService {

    private final InternalSysUserFeign feign;

    public InternalSysUserServiceImpl(InternalSysUserFeign feign) {
        this.feign = feign;
    }

    @Override
    public void deduct(String userId, BigDecimal amount) {
        Result<Boolean> result = feign.deduct(new InternalSysUserBalanceDto(userId, amount));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
    }

    @Override
    public void recharge(String userId, BigDecimal amount) {
        Result<Boolean> result = feign.deduct(new InternalSysUserBalanceDto(userId, amount));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
    }

    @Override
    public InternalLoginInfoDto checkLogin(InternalSysUserCheckLoginDto dto) {
        Result<InternalLoginInfoDto> result = feign.checkLogin(dto);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
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
    public String add(InternalSysUserAddDto dto) {
        Result<String> result = feign.add(dto);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }
}
