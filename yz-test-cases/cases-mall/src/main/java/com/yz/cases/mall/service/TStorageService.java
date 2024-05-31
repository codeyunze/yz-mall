package com.yz.cases.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.cases.mall.dto.TStorageAddDto;
import com.yz.cases.mall.entity.TStorage;

/**
 * 库存信息(TStock)表服务接口
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
public interface TStorageService extends IService<TStorage> {

    Integer save(TStorageAddDto dto);

}

