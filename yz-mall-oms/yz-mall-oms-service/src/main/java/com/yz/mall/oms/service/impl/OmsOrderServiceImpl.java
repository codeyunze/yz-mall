package com.yz.mall.oms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.oms.dto.*;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import com.yz.mall.oms.enums.OmsOrderStatusEnum;
import com.yz.mall.oms.enums.OmsPayTypeEnum;
import com.yz.mall.oms.mapper.OmsOrderMapper;
import com.yz.mall.oms.service.OmsOrderRelationProductService;
import com.yz.mall.oms.service.OmsOrderService;
import com.yz.mall.oms.vo.OmsOrderDetailVo;
import com.yz.mall.oms.vo.OmsOrderProductVo;
import com.yz.mall.oms.vo.OmsOrderVo;
import com.yz.mall.pms.dto.InternalPmsProductSlimVo;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.service.InternalPmsProductService;
import com.yz.mall.pms.service.InternalPmsShopCartService;
import com.yz.mall.pms.service.InternalPmsStockService;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.exception.BusinessException;
import com.yz.mall.web.exception.DataNotExistException;
import com.yz.unqid.service.InternalUnqidService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单信息表(OmsOrder)表服务实现类
 *
 * @author yunze
 * @since 2025-01-30 19:12:59
 */
@Service
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements OmsOrderService {

    private final InternalUnqidService internalUnqidService;

    private final OmsOrderRelationProductService omsOrderRelationProductService;

    private final InternalPmsStockService internalPmsStockService;

    private final InternalPmsShopCartService internalPmsShopCartService;

    private final InternalPmsProductService internalPmsProductService;

    public OmsOrderServiceImpl(InternalUnqidService internalUnqidService
            , OmsOrderRelationProductService omsOrderRelationProductService
            , InternalPmsStockService internalPmsStockService
            , InternalPmsShopCartService internalPmsShopCartService
            , InternalPmsProductService internalPmsProductService) {
        this.internalUnqidService = internalUnqidService;
        this.omsOrderRelationProductService = omsOrderRelationProductService;
        this.internalPmsStockService = internalPmsStockService;
        this.internalPmsShopCartService = internalPmsShopCartService;
        this.internalPmsProductService = internalPmsProductService;
    }

    @Transactional
    @Override
    public Long generateOrder(InternalOmsOrderByCartDto dto) {
        OmsOrder bo = new OmsOrder();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setCreateId(dto.getUserId());

        // 省市区年月日000001
        String prefix = dto.getReceiverProvince().substring(0, 6) + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        String orderCode = internalUnqidService.generateSerialNumber(prefix, 6);
        bo.setOrderCode(orderCode);
        // 订单状态为待付款
        bo.setOrderStatus(OmsOrderStatusEnum.PENDING_PAYMENT.getStatus());
        bo.setPayType(OmsPayTypeEnum.PENDING_PAY.getType());

        // 去查询购物车里的商品信息（商品Id、商品数量、商品优惠金额、商品优惠后的实际价格）
        // Map<Long, InternalPmsCartDto> cartProductMap = internalPmsShopCartService.getCartByIds(bo.getUserId(), dto.getProducts());

        // TODO 2025/1/31 yunze 暂时先直接扣除商品库存，应该是锁定商品库存的，等支付订单之后再扣减库存
        // 扣减库存信息
        List<InternalPmsStockDto> deductStocks = new ArrayList<>();

        // 订单商品信息入库
        List<Long> productIds = dto.getProducts().stream().map(InternalOmsOrderProductDto::getProductId).collect(Collectors.toList());
        List<InternalPmsProductSlimVo> productsInfo = internalPmsProductService.getProductByProductIds(productIds);
        Map<Long, InternalPmsProductSlimVo> productMap = productsInfo.stream().collect(Collectors.toMap(InternalPmsProductSlimVo::getId, t -> t));

        List<OmsOrderRelationProduct> products = new ArrayList<>();
        dto.getProducts().forEach(product -> {
            // 购物车里商品信息
            OmsOrderRelationProduct relationProduct = new OmsOrderRelationProduct();
            BeanUtils.copyProperties(product, relationProduct);
            relationProduct.setProductQuantity(product.getProductQuantity());
            relationProduct.setOrderId(bo.getId());
            relationProduct.setProductName(productMap.get(product.getProductId()).getProductName());
            relationProduct.setProductPrice(productMap.get(product.getProductId()).getProductPrice());
            products.add(relationProduct);

            InternalPmsStockDto stock = new InternalPmsStockDto();
            stock.setProductId(product.getProductId());
            stock.setQuantity(product.getProductQuantity());
            stock.setRemark("订单扣减库存");
            stock.setOrderId(bo.getId());
            deductStocks.add(stock);
        });

        // 扣除商品库存
        internalPmsStockService.deductBatch(deductStocks);
        // 订单信息入库
        baseMapper.insert(bo);
        // 订单详情信息入库
        omsOrderRelationProductService.saveBatch(products);
        // 清理购物车中下单的商品
        internalPmsShopCartService.removeCartByProductIds(bo.getUserId(), deductStocks);
        return bo.getId();
    }

    @Transactional
    @Override
    public Long add(InternalOmsOrderDto dto) {
        OmsOrder bo = new OmsOrder();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setCreateId(dto.getUserId());

        // 省市区年月日000001
        String prefix = dto.getReceiverProvince().substring(0, 6) + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        String orderCode = internalUnqidService.generateSerialNumber(prefix, 6);
        bo.setOrderCode(orderCode);
        // 订单状态为待付款
        bo.setOrderStatus(OmsOrderStatusEnum.PENDING_PAYMENT.getStatus());
        bo.setPayType(OmsPayTypeEnum.PENDING_PAY.getType());

        // TODO 2025/1/31 yunze 暂时先直接扣除商品库存，应该是锁定商品库存的
        // 扣减库存信息
        List<InternalPmsStockDto> deductStocks = new ArrayList<>();

        // 订单商品信息入库
        List<OmsOrderRelationProduct> products = new ArrayList<>();
        for (InternalOmsOrderProductDto product : dto.getProducts()) {
            OmsOrderRelationProduct relationProduct = new OmsOrderRelationProduct();
            BeanUtils.copyProperties(product, relationProduct);
            relationProduct.setOrderId(bo.getId());
            products.add(relationProduct);

            InternalPmsStockDto stock = new InternalPmsStockDto();
            stock.setProductId(product.getProductId());
            stock.setQuantity(product.getProductQuantity());
            stock.setRemark("订单扣减库存");
            stock.setOrderId(bo.getId());
            deductStocks.add(stock);
        }

        // 扣除商品库存
        internalPmsStockService.deductBatch(deductStocks);
        // 订单信息入库
        baseMapper.insert(bo);
        // 订单详情信息入库
        omsOrderRelationProductService.saveBatch(products);
        return bo.getId();
    }

    @Override
    public Page<OmsOrderVo> page(PageFilter<OmsOrderQueryDto> filter) {
        // 查询订单信息
        Page<OmsOrderVo> page = baseMapper.selectPageByFilter(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
        /*if (page.getTotal() == 0) {
            return page;
        }

        // 查询订单信息里的商品信息
        List<Long> orderIds = page.getRecords().stream().map(OmsOrderVo::getId).collect(Collectors.toList());
        Map<Long, List<OmsOrderProductVo>> orderProductByOrderIdsMap = omsOrderRelationProductService.getOrderProductByOrderIds(orderIds);

        // 数据组装
        page.getRecords().forEach(item -> {
            if (!CollectionUtils.isEmpty(orderProductByOrderIdsMap.get(item.getId()))) {
                List<OmsOrderProductVo> productVos = orderProductByOrderIdsMap.get(item.getId());
                item.setProducts(productVos);
            }
        });*/

        return page;
    }

    @Override
    public OmsOrderDetailVo get(Long userId, OmsOrderQuerySlimDto query) {
        LambdaQueryWrapper<OmsOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OmsOrder::getUserId, userId);
        queryWrapper.eq(StringUtils.hasText(query.getOrderId()), OmsOrder::getId, query.getOrderId());
        queryWrapper.eq(StringUtils.hasText(query.getOrderCode()), OmsOrder::getOrderCode, query.getOrderCode());
        OmsOrder omsOrder = baseMapper.selectOne(queryWrapper);
        if (omsOrder == null) {
            throw new DataNotExistException("订单信息不存在");
        }
        OmsOrderDetailVo detailVo = new OmsOrderDetailVo();
        BeanUtils.copyProperties(omsOrder, detailVo);

        // 查询订单商品信息
        List<OmsOrderProductVo> products = omsOrderRelationProductService.getOrderProductsByOrderId(omsOrder.getId());
        detailVo.setProducts(products);
        return detailVo;
    }

    @Override
    public boolean cancelById(Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<OmsOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OmsOrder::getId, id);
        queryWrapper.eq(OmsOrder::getUserId, userId);
        OmsOrder order = baseMapper.selectOne(queryWrapper);
        if (order == null) {
            throw new DataNotExistException("订单信息不存在，无法操作取消订单");
        }
        order.setOrderStatus(OmsOrderStatusEnum.ORDER_CLOSED.getStatus());
        order.setUpdateId(userId);
        order.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(order) > 0;
    }

    @Override
    public boolean payOrderById(Long id, Integer payType) {
        long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<OmsOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OmsOrder::getId, id);
        queryWrapper.eq(OmsOrder::getUserId, userId);
        OmsOrder order = baseMapper.selectOne(queryWrapper);
        if (order == null) {
            throw new DataNotExistException("订单信息不存在，无法支付订单");
        }
        if (0 != order.getPayType()) {
            throw new BusinessException("订单" + order.getOrderCode() +"已支付，无需重复支付");
        }
        order.setPayType(payType);
        order.setOrderStatus(OmsOrderStatusEnum.PENDING_SHIPMENT.getStatus());
        order.setUpdateId(userId);
        order.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(order) > 0;
    }

}

