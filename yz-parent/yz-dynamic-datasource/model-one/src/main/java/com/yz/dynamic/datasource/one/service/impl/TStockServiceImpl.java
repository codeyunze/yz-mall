package com.yz.dynamic.datasource.one.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.dynamic.datasource.one.annotation.DS;
import com.yz.dynamic.datasource.one.dto.StockAddDto;
import com.yz.dynamic.datasource.one.mapper.TStockMapper;
import com.yz.dynamic.datasource.one.entity.TStock;
import com.yz.dynamic.datasource.one.service.TStockService;
import com.yz.tools.enums.DataSourceTypeEnum;
import org.springframework.stereotype.Service;

/**
 * 库存信息(TStock)表服务实现类
 *
 * @author yunze
 * @since 2023-10-29 18:00:41
 */
@Service("tStockService")
public class TStockServiceImpl extends ServiceImpl<TStockMapper, TStock> implements TStockService {

    @DS(DataSourceTypeEnum.stock)
    @Override
    public Integer save(StockAddDto dto) {
        dto.setId(IdUtil.getSnowflake().nextId());
        return baseMapper.save(dto);
    }
}

