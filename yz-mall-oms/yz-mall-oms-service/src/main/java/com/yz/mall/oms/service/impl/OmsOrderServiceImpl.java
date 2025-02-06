package com.yz.mall.oms.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.oms.dto.InternalOmsOrderByCartDto;
import com.yz.mall.oms.dto.InternalOmsOrderDto;
import com.yz.mall.oms.dto.InternalOmsOrderProductDto;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.mall.oms.entity.OmsOrderRelationProduct;
import com.yz.mall.oms.enums.OmsOrderStatusEnum;
import com.yz.mall.oms.enums.OmsPayTypeEnum;
import com.yz.mall.oms.mapper.OmsOrderMapper;
import com.yz.mall.oms.service.OmsOrderRelationProductService;
import com.yz.mall.oms.service.OmsOrderService;
import com.yz.mall.pms.dto.InternalPmsCartDto;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.service.InternalPmsShopCartService;
import com.yz.mall.pms.service.InternalPmsStockService;
import com.yz.unqid.service.InternalUnqidService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public OmsOrderServiceImpl(InternalUnqidService internalUnqidService
            , OmsOrderRelationProductService omsOrderRelationProductService
            , InternalPmsStockService internalPmsStockService
            , InternalPmsShopCartService internalPmsShopCartService) {
        this.internalUnqidService = internalUnqidService;
        this.omsOrderRelationProductService = omsOrderRelationProductService;
        this.internalPmsStockService = internalPmsStockService;
        this.internalPmsShopCartService = internalPmsShopCartService;
    }

    @Transactional
    @Override
    public Long generateOrder(InternalOmsOrderByCartDto dto) {
        OmsOrder bo = new OmsOrder();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setCreatedId(dto.getUserId());

        // 省市区年月日000001
        String prefix = dto.getReceiverProvince().substring(0, 6) + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        String orderCode = internalUnqidService.generateSerialNumber(prefix, 6);
        bo.setOrderCode(orderCode);
        // 订单状态为待付款
        bo.setOrderStatus(OmsOrderStatusEnum.PENDING_PAYMENT.getStatus());
        bo.setPayType(OmsPayTypeEnum.PENDING_PAY.getType());

        // 去查询购物车里的商品信息（商品Id、商品数量、商品优惠金额、商品优惠后的实际价格）
        Map<Long, InternalPmsCartDto> cartProductMap = internalPmsShopCartService.getCartByIds(bo.getUserId(), dto.getProducts());

        // TODO 2025/1/31 yunze 暂时先直接扣除商品库存，应该是锁定商品库存的，等支付订单之后再扣减库存
        // 扣减库存信息
        List<InternalPmsStockDto> deductStocks = new ArrayList<>();

        // 订单商品信息入库
        List<OmsOrderRelationProduct> products = new ArrayList<>();
        for (Long cartId : dto.getProducts()) {
            // 购物车里商品信息
            InternalPmsCartDto product = cartProductMap.get(cartId);
            OmsOrderRelationProduct relationProduct = new OmsOrderRelationProduct();
            BeanUtils.copyProperties(product, relationProduct);
            relationProduct.setProductQuantity(product.getQuantity());
            relationProduct.setOrderId(bo.getId());

            products.add(relationProduct);

            InternalPmsStockDto stock = new InternalPmsStockDto();
            stock.setProductId(product.getProductId());
            stock.setQuantity(product.getQuantity());
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
        // 清理购物车中下单的商品
        internalPmsShopCartService.removeCartByIds(bo.getUserId(), dto.getProducts());
        return bo.getId();
    }

    @Transactional
    @Override
    public Long add(InternalOmsOrderDto dto) {
        OmsOrder bo = new OmsOrder();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setCreatedId(dto.getUserId());

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

}

