package com.yz.dynamic.datasource.one.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.dynamic.datasource.one.annotation.DS;
import com.yz.dynamic.datasource.one.dto.TStorageAddDto;
import com.yz.dynamic.datasource.one.mapper.TStorageMapper;
import com.yz.dynamic.datasource.one.entity.TStorage;
import com.yz.dynamic.datasource.one.service.TStorageService;
import com.yz.tools.enums.DataSourceTypeEnum;
import org.springframework.stereotype.Service;

/**
 * 库存信息(TStorage)表服务实现类
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
@Service("tStorageService")
public class TStorageServiceImpl extends ServiceImpl<TStorageMapper, TStorage> implements TStorageService {

    @DS(DataSourceTypeEnum.storage)
    @Override
    public Integer save(TStorageAddDto dto) {
        dto.setId(IdUtil.getSnowflake().nextId());
        return baseMapper.save(dto);
    }
}

