package com.yz.mall.oms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.oms.dto.OmsOrderItemDto;
import com.yz.mall.oms.dto.OmsOrderRelationProductAddDto;
import com.yz.mall.oms.dto.OmsOrderProductRelationQueryDto;
import com.yz.mall.oms.dto.OmsOrderRelationProductUpdateDto;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import com.yz.mall.oms.mapper.OmsOrderProductRelationMapper;
import com.yz.mall.oms.service.OmsOrderProductRelationService;
import com.yz.mall.web.common.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单商品关联表(OmsOrderProductRelation)表服务实现类
 *
 * @author yunze
 * @since 2024-06-18 12:51:39
 */
@Service
public class OmsOrderProductRelationServiceImpl extends ServiceImpl<OmsOrderProductRelationMapper, OmsOrderRelationProduct> implements OmsOrderProductRelationService {

    @Override
    public Long save(OmsOrderRelationProductAddDto dto) {
        OmsOrderRelationProduct bo = new OmsOrderRelationProduct();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean saveBatch(Long orderId, List<OmsOrderItemDto> array) {
        List<OmsOrderRelationProduct> bos = array.stream().map(t -> {
            OmsOrderRelationProduct bo = new OmsOrderRelationProduct();
            BeanUtils.copyProperties(t, bo);
            bo.setId(IdUtil.getSnowflakeNextId());
            bo.setOrderId(orderId);
            return bo;
        }).collect(Collectors.toList());
        return super.saveBatch(bos);
    }

    @Override
    public boolean update(OmsOrderRelationProductUpdateDto dto) {
        OmsOrderRelationProduct bo = new OmsOrderRelationProduct();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<OmsOrderRelationProduct> page(PageFilter<OmsOrderProductRelationQueryDto> filter) {
        LambdaQueryWrapper<OmsOrderRelationProduct> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

