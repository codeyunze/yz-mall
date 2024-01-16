package com.yz.mall.seata.tcc.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.seata.tcc.account.entity.TccAccount;
import com.yz.mall.seata.tcc.account.mapper.TccAccountMapper;
import com.yz.mall.seata.tcc.account.service.TccAccountService;
import io.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 账号信息(TAccount)表服务实现类
 *
 * @author yunze
 * @since 2023-11-07 23:21:27
 */
@Service
public class TccAccountServiceImpl extends ServiceImpl<TccAccountMapper, TccAccount> implements TccAccountService {

    @Transactional
    @Override
    public boolean deduct(Long accountId, BigDecimal amountToDeduct) {
        TccAccount tAccount = baseMapper.selectById(accountId);
        if (tAccount == null) {
            throw new RuntimeException("不存在账号信息");
        }
        if (amountToDeduct.compareTo(tAccount.getCashBalance()) > 0) {
            throw new RuntimeException("余额不足");
        }
        // 先冻结余额里指定金额
        return baseMapper.freezeBalance(accountId, amountToDeduct) > 0;
    }

    @Override
    public boolean commit(BusinessActionContext actionContext) {
        Long accountId = Long.parseLong(Objects.requireNonNull(actionContext.getActionContext("accountId")).toString());
        Object money = actionContext.getActionContext("amountToDeduct");
        BigDecimal amountToDeduct = new BigDecimal(String.valueOf(money));
        // 实际扣减已经被冻结的金额
        return baseMapper.deductFreezeBalance(accountId, amountToDeduct) > 0;
    }

    @Override
    public boolean rollback(BusinessActionContext actionContext) {
        Long accountId = Long.parseLong(Objects.requireNonNull(actionContext.getActionContext("accountId")).toString());
        Object money = actionContext.getActionContext("amountToDeduct");
        BigDecimal amountToDeduct = new BigDecimal(String.valueOf(money));
        // 回滚时解冻被冻结的金额
        return baseMapper.unfreezeBalance(accountId, amountToDeduct) > 0;
    }
}

