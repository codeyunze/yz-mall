package com.yz.dynamic.datasource.one.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.dynamic.datasource.one.annotation.DS;
import com.yz.dynamic.datasource.one.dto.ProductAddDto;
import com.yz.dynamic.datasource.one.mapper.TProductMapper;
import com.yz.dynamic.datasource.one.entity.TProduct;
import com.yz.dynamic.datasource.one.service.TProductService;
import com.yz.tools.enums.DataSourceTypeEnum;
import org.springframework.stereotype.Service;

/**
 * 商品信息(TProduct)表服务实现类
 *
 * @author yunze
 * @since 2023-10-29 18:01:13
 */
@Service("tProductService")
public class TProductServiceImpl extends ServiceImpl<TProductMapper, TProduct> implements TProductService {

    @DS("product")
    @Override
    public Integer save(ProductAddDto dto) {
        dto.setId(IdUtil.getSnowflake().nextId());
        return baseMapper.save(dto);
    }
}

