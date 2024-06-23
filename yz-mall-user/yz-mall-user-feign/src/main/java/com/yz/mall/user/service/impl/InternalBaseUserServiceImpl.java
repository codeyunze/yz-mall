package com.yz.mall.user.service.impl;

import com.yz.mall.user.dto.InternalBaseUserBalanceDto;
import com.yz.mall.user.feign.InternalBaseUserFeign;
import com.yz.mall.user.service.InternalBaseUserService;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author yunze
 * @date 2024/6/22 23:42
 */
@Service
public class InternalBaseUserServiceImpl implements InternalBaseUserService {

    private final InternalBaseUserFeign feign;

    public InternalBaseUserServiceImpl(InternalBaseUserFeign feign) {
        this.feign = feign;
    }

    @Override
    public void deduct(String userId, BigDecimal amount) {
        Result<Boolean> deducted = feign.deduct(new InternalBaseUserBalanceDto(userId, amount));
        // if (!CodeEnum.SUCCESS.get().equals(deducted.getCode())) {
        // }
    }

    @Override
    public void recharge(String userId, BigDecimal amount) {
        Result<Boolean> deducted = feign.deduct(new InternalBaseUserBalanceDto(userId, amount));
    }
}
