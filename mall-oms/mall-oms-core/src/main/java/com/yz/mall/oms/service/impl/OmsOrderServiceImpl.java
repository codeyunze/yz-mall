package com.yz.mall.oms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.base.exception.DataNotExistException;
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
import com.yz.mall.oms.vo.OmsOrderSlimVo;
import com.yz.mall.oms.vo.OmsOrderVo;
import com.yz.mall.pms.dto.InternalPmsProductSlimVo;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.service.InternalPmsProductService;
import com.yz.mall.pms.service.InternalPmsShopCartService;
import com.yz.mall.pms.service.InternalPmsStockService;
import com.yz.mall.serial.service.ExtendSerialService;
import com.yz.mall.sys.service.ExtendSysAreaService;
// import com.yz.mall.sys.service.InternalSysFilesService;
import com.yz.mall.sys.service.ExtendSysUserService;
import com.yz.mall.sys.vo.InternalQofFileInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单信息表(OmsOrder)表服务实现类
 *
 * @author yunze
 * @since 2025-01-30 19:12:59
 */
@Service
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements OmsOrderService {

    private final ExtendSerialService extendSerialService;

    private final OmsOrderRelationProductService omsOrderRelationProductService;

    private final InternalPmsStockService internalPmsStockService;

    private final InternalPmsShopCartService internalPmsShopCartService;

    private final InternalPmsProductService internalPmsProductService;

    private final ExtendSysUserService extendSysUserService;

    // private final InternalSysFilesService internalSysFilesService;

    private final ExtendSysAreaService internalSysAreaService;

    public OmsOrderServiceImpl(ExtendSerialService extendSerialService
            , OmsOrderRelationProductService omsOrderRelationProductService
            , InternalPmsStockService internalPmsStockService
            , InternalPmsShopCartService internalPmsShopCartService
            , InternalPmsProductService internalPmsProductService
            , ExtendSysUserService extendSysUserService
            // , InternalSysFilesService internalSysFilesService
            , ExtendSysAreaService internalSysAreaService) {
        this.extendSerialService = extendSerialService;
        this.omsOrderRelationProductService = omsOrderRelationProductService;
        this.internalPmsStockService = internalPmsStockService;
        this.internalPmsShopCartService = internalPmsShopCartService;
        this.internalPmsProductService = internalPmsProductService;
        this.extendSysUserService = extendSysUserService;
        // this.internalSysFilesService = internalSysFilesService;
        this.internalSysAreaService = internalSysAreaService;
    }

    @Transactional
    @Override
    public OmsOrderSlimVo generateOrder(InternalOmsOrderByCartDto dto) {
        OmsOrder bo = new OmsOrder();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setCreateId(dto.getUserId());

        // 省市区年月日000001
        String prefix = dto.getReceiverProvince().substring(0, 6) + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN).substring(2);
        String orderCode = extendSerialService.generateNumber(prefix, 6);
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

        // 订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (InternalOmsOrderProductDto product : dto.getProducts()) {
            // 订单下的商品信息
            OmsOrderRelationProduct relationProduct = new OmsOrderRelationProduct();
            BeanUtils.copyProperties(product, relationProduct);
            relationProduct.setProductQuantity(product.getProductQuantity());
            relationProduct.setOrderId(bo.getId());
            relationProduct.setProductName(productMap.get(product.getProductId()).getProductName());
            relationProduct.setProductPrice(productMap.get(product.getProductId()).getProductPrice());
            relationProduct.setAlbumPics(productMap.get(product.getProductId()).getAlbumPics());
            products.add(relationProduct);

            InternalPmsStockDto stock = new InternalPmsStockDto();
            stock.setProductId(product.getProductId());
            stock.setQuantity(product.getProductQuantity());
            stock.setRemark("订单扣减库存");
            stock.setOrderId(bo.getId());
            deductStocks.add(stock);

            totalAmount = totalAmount.add(relationProduct.getProductPrice().multiply(BigDecimal.valueOf(product.getProductQuantity())));
        }

        bo.setTotalAmount(totalAmount);
        bo.setPayAmount(totalAmount);

        // 扣除商品库存
        internalPmsStockService.deductBatch(deductStocks);
        // 订单信息入库
        baseMapper.insert(bo);
        // 订单详情信息入库
        omsOrderRelationProductService.saveBatch(products);
        // 清理购物车中下单的商品
        internalPmsShopCartService.removeCartByProductIds(bo.getUserId(), deductStocks);
        return new OmsOrderSlimVo(bo.getId(), bo.getOrderCode());
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
        String orderCode = extendSerialService.generateNumber(prefix, 6);
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
        detailVo.setReceiverProvinceName(internalSysAreaService.getById(detailVo.getReceiverProvince()).getName());
        detailVo.setReceiverCityName(internalSysAreaService.getById(detailVo.getReceiverCity()).getName());
        detailVo.setReceiverDistrictName(internalSysAreaService.getById(detailVo.getReceiverDistrict()).getName());

        // 查询订单商品信息
        List<OmsOrderProductVo> products = omsOrderRelationProductService.getOrderProductsByOrderId(omsOrder.getId());
        detailVo.setProducts(products);

        List<Long> fileIds = new ArrayList<>();
        for (OmsOrderProductVo product : products) {
            if (!StringUtils.hasText(product.getAlbumPics())) {
                continue;
            }
            fileIds.addAll(Arrays.stream(product.getAlbumPics().split(",")).map(Long::parseLong).collect(Collectors.toList()));
        }

        // 获取商品文件信息
        // List<InternalQofFileInfoVo> filesInfo = internalSysFilesService.getFileInfoByFileIdsAndPublic(fileIds);
        List<InternalQofFileInfoVo> filesInfo = new ArrayList<>();
        if (CollectionUtils.isEmpty(filesInfo)) {
            return detailVo;
        }

        for (OmsOrderProductVo product : detailVo.getProducts()) {
            assembleProductImage(product, filesInfo);
        }
        return detailVo;
    }

    /**
     * 组装订单关联的产品的图片预览地址
     *
     * @param vo                    订单里产品信息
     * @param qofFileInfoVos        所有图片预览数据
     */
    private void assembleProductImage(OmsOrderProductVo vo
            , List<InternalQofFileInfoVo> qofFileInfoVos) {
        if (!StringUtils.hasText(vo.getAlbumPics())) {
            return;
        }
        for (InternalQofFileInfoVo fileInfoVo : qofFileInfoVos) {
            if (StringUtils.hasText(vo.getPreviewAddress())
                    || !fileInfoVo.getFileId().equals(Long.parseLong(vo.getAlbumPics().split(",")[0]))) {
                continue;
            }
            vo.setPreviewAddress(fileInfoVo.getPreviewAddress());
        }
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

    @Transactional(rollbackFor = Exception.class)
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
            throw new BusinessException("订单" + order.getOrderCode() + "已支付，无需重复支付");
        }

        // 修改订单状态
        order.setPayType(payType);
        order.setOrderStatus(OmsOrderStatusEnum.PENDING_SHIPMENT.getStatus());
        order.setUpdateId(userId);
        order.setUpdateTime(LocalDateTime.now());

        // 扣减用户余额
        extendSysUserService.deduct(userId, order.getPayAmount());
        return baseMapper.updateById(order) > 0;
    }

}

