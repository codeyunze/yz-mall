package com.yz.nacos.mall.storage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.nacos.mall.storage.entity.TStorage;

/**
 * 库存信息(TStock)表服务接口
 *
 * @author yunze
 * @since 2023-11-05 15:59:36
 */
public interface TStorageService extends IService<TStorage> {

    boolean deduct(Long productId, Integer num);
}

