package com.yz.mall.seata.tcc.storage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.seata.tcc.storage.entity.TccStorage;
import com.yz.mall.seata.tcc.storage.mapper.TccStorageMapper;
import com.yz.mall.seata.tcc.storage.service.TccStorageService;
import io.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存信息(TccStorage)表服务实现类
 *
 * @author yunze
 * @since 2023-11-05 15:59:36
 */
@Service
public class TccStorageServiceImpl extends ServiceImpl<TccStorageMapper, TccStorage> implements TccStorageService {

    @Transactional
    @Override
    public boolean deduct(Long productId, Integer deductNum) {
        Integer freezeStorage = baseMapper.freezeStorage(productId, deductNum);
        if (freezeStorage <= 0) {
            throw new RuntimeException("库存扣减失败");
        }
        return true;
    }

    @Override
    public boolean commit(BusinessActionContext actionContext) {
        Long productId = (Long) actionContext.getActionContext("productId");
        Integer deductNum = (Integer) actionContext.getActionContext("deductNum");
        return baseMapper.deductFreezeStorage(productId, deductNum) > 0;
    }

    @Override
    public boolean rollback(BusinessActionContext actionContext) {
        Long productId = (Long) actionContext.getActionContext("productId");
        Integer deductNum = (Integer) actionContext.getActionContext("deductNum");
        return baseMapper.unfreezeStorage(productId, deductNum) > 0;
    }
}

