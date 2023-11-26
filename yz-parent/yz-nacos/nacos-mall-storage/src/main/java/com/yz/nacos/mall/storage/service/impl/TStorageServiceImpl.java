package com.yz.nacos.mall.storage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.nacos.mall.storage.entity.TStorage;
import com.yz.nacos.mall.storage.mapper.TStorageMapper;
import com.yz.nacos.mall.storage.service.TStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存信息(TStock)表服务实现类
 *
 * @author yunze
 * @since 2023-11-05 15:59:36
 */
@Service("tStockService")
public class TStorageServiceImpl extends ServiceImpl<TStorageMapper, TStorage> implements TStorageService {

    @Transactional
    @Override
    public boolean deduct(Long productId, Integer num) {
        boolean deduct = baseMapper.deduct(productId, num);
        if (!deduct) {
            throw new RuntimeException("库存扣减失败");
        }
        return true;
    }
}

