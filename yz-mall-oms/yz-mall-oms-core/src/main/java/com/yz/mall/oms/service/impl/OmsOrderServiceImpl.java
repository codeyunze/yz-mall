package com.yz.mall.oms.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.oms.dto.*;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.mall.oms.mapper.OmsOrderMapper;
import com.yz.mall.oms.service.OmsOrderProductRelationService;
import com.yz.mall.oms.service.OmsOrderService;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.service.InternalPmsStockService;
import com.yz.mall.sys.service.InternalSysUserService;
import com.yz.mall.web.common.PageFilter;
import com.yz.unqid.service.InternalUnqidService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单信息表(OmsOrder)表服务实现类
 *
 * @author yunze
 * @since 2024-06-18 12:49:55
 */

@Service
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements OmsOrderService {

    private final OmsOrderProductRelationService omsOrderProductRelationService;

    private final InternalUnqidService internalUnqidService;

    private final InternalPmsStockService internalPmsStockService;

    private final InternalSysUserService internalBaseUserService;

    public OmsOrderServiceImpl(OmsOrderProductRelationService omsOrderProductRelationService, InternalUnqidService internalUnqidService, InternalPmsStockService internalPmsStockService, InternalSysUserService internalBaseUserService) {
        this.omsOrderProductRelationService = omsOrderProductRelationService;
        this.internalUnqidService = internalUnqidService;
        this.internalPmsStockService = internalPmsStockService;
        this.internalBaseUserService = internalBaseUserService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long generateOrder(OmsOrderGenerateDto dto) {
        OmsOrder order = new OmsOrder();
        BeanUtils.copyProperties(dto, order);
        order.setId(IdUtil.getSnowflakeNextId());
        // TODO: 2024/6/18 星期二 yunze 序号自动有序的生成
        // 省市区年月日000001
        String prefix = dto.getReceiverProvince().substring(0, 6) + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        // String orderCode = dto.getReceiverProvince().substring(0, 6) + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN) + "000002";
        String orderCode = internalUnqidService.generateSerialNumber(prefix, 6);
        order.setOrderCode(orderCode);
        // 订单信息
        baseMapper.insert(order);

        // 订单关联商品信息
        omsOrderProductRelationService.saveBatch(order.getId(), dto.getOmsOrderItems());

        // 扣减库存
        List<InternalPmsStockDto> productStocks = dto.getOmsOrderItems().stream().map(t -> new InternalPmsStockDto(t.getProductId(), t.getProductQuantity())).collect(Collectors.toList());
        internalPmsStockService.deductBatch(productStocks);

        // 扣减用户余额
        // internalBaseUserService.deduct(dto.getUserId(), dto.getPayAmount());

        return order.getId();
    }

    @Override
    public Long save(OmsOrderAddDto dto) {
        OmsOrder bo = new OmsOrder();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(OmsOrderUpdateDto dto) {
        OmsOrder bo = new OmsOrder();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @DS("slave")
    @Override
    public Page<OmsOrder> page(PageFilter<OmsOrderQueryDto> filter) {
        LambdaQueryWrapper<OmsOrder> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

