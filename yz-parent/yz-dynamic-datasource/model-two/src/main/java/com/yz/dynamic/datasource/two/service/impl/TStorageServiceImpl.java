package com.yz.dynamic.datasource.two.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.dynamic.datasource.two.mapper.TStorageMapper;
import com.yz.dynamic.datasource.two.entity.TStorage;
import com.yz.dynamic.datasource.two.service.TStorageService;
import org.springframework.stereotype.Service;

/**
 * 库存信息(TStorage)表服务实现类
 *
 * @author yunze
 * @since 2023-10-31 00:02:31
 */
@DS("storage")
@Service("tStorageService")
public class TStorageServiceImpl extends ServiceImpl<TStorageMapper, TStorage> implements TStorageService {

}

