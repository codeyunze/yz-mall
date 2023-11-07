package com.yz.nacos.mall.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.nacos.mall.account.entity.TAccount;
import com.yz.nacos.mall.account.mapper.TAccountMapper;
import com.yz.nacos.mall.account.service.TAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 账号信息(TAccount)表服务实现类
 *
 * @author yunze
 * @since 2023-11-07 23:21:27
 */
@Service("tAccountService")
public class TAccountServiceImpl extends ServiceImpl<TAccountMapper, TAccount> implements TAccountService {

    @Transactional
    @Override
    public boolean deduct(Long accountId, BigDecimal amount) {
        TAccount tAccount = baseMapper.selectById(accountId);
        if (tAccount == null) {
            throw new RuntimeException("不存在账号信息");
        }
        if (amount.compareTo(tAccount.getCashBalance()) > 0) {
            throw new RuntimeException("余额不足");
        }
        Integer deduct = baseMapper.deduct(accountId, amount);
        return deduct > 0;
    }
}

