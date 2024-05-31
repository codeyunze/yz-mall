package com.yz.cases.mall.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.cases.mall.dto.TStorageAddDto;
import com.yz.cases.mall.entity.TStorage;
import com.yz.cases.mall.mapper.TStorageMapper;
import com.yz.cases.mall.service.TStorageService;
import org.springframework.stereotype.Service;

/**
 * 库存信息(TStorage)表服务实现类
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
@Service("tStorageService")
public class TStorageServiceImpl extends ServiceImpl<TStorageMapper, TStorage> implements TStorageService {

    @Override
    public Integer save(TStorageAddDto dto) {
        dto.setId(IdUtil.getSnowflake().nextId());
        return baseMapper.save(dto);
    }
}

