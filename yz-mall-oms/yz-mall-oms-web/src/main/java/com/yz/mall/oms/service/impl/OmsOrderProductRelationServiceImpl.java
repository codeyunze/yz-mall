package com.yz.mall.oms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.oms.dto.OmsOrderItemDto;
import com.yz.mall.oms.dto.OmsOrderProductRelationAddDto;
import com.yz.mall.oms.dto.OmsOrderProductRelationQueryDto;
import com.yz.mall.oms.dto.OmsOrderProductRelationUpdateDto;
import com.yz.mall.oms.entity.OmsOrderProductRelation;
import com.yz.mall.oms.mapper.OmsOrderProductRelationMapper;
import com.yz.mall.oms.service.OmsOrderProductRelationService;
import com.yz.tools.PageFilter;
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
public class OmsOrderProductRelationServiceImpl extends ServiceImpl<OmsOrderProductRelationMapper, OmsOrderProductRelation> implements OmsOrderProductRelationService {

    @Override
    public String save(OmsOrderProductRelationAddDto dto) {
        OmsOrderProductRelation bo = new OmsOrderProductRelation();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextIdStr());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean saveBatch(String orderId, List<OmsOrderItemDto> dtos) {
        List<OmsOrderProductRelation> bos = dtos.stream().map(t -> {
            OmsOrderProductRelation bo = new OmsOrderProductRelation();
            BeanUtils.copyProperties(t, bo);
            bo.setId(IdUtil.getSnowflakeNextIdStr());
            bo.setOrderId(orderId);
            return bo;
        }).collect(Collectors.toList());
        return super.saveBatch(bos);
    }

    @Override
    public boolean update(OmsOrderProductRelationUpdateDto dto) {
        OmsOrderProductRelation bo = new OmsOrderProductRelation();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<OmsOrderProductRelation> page(PageFilter<OmsOrderProductRelationQueryDto> filter) {
        LambdaQueryWrapper<OmsOrderProductRelation> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

