package com.yz.dynamic.datasource.one.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.dynamic.datasource.one.dto.TStorageAddDto;
import com.yz.dynamic.datasource.one.entity.TStorage;

/**
 * 库存信息(TStock)表服务接口
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
public interface TStorageService extends IService<TStorage> {

    Integer save(TStorageAddDto dto);

}

