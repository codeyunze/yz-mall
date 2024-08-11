package com.yz.mall.user.service.impl;

import com.yz.mall.user.service.BaseUserService;
import com.yz.mall.user.service.InternalBaseUserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author yunze
 * @date 2024/6/19 星期三 23:49
 */
@Service
public class InternalBaseUserServiceImpl implements InternalBaseUserService {

    private final BaseUserService service;

    public InternalBaseUserServiceImpl(BaseUserService service) {
        this.service = service;
    }

    @Override
    public void deduct(String userId, BigDecimal amount) {
        this.service.deduct(userId, amount);
    }

    @Override
    public void recharge(String userId, BigDecimal amount) {
        this.service.recharge(userId, amount);
    }
}
