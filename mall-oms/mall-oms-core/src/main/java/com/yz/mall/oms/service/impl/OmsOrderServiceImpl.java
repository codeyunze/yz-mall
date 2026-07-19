package com.yz.mall.oms.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import com.yz.mall.pms.dto.ExtendPmsProductSlimVo;
import com.yz.mall.pms.dto.ExtendPmsStockDto;
import com.yz.mall.pms.service.ExtendPmsProductService;
import com.yz.mall.pms.service.ExtendPmsShopCartService;
import com.yz.mall.pms.service.ExtendPmsStockService;
import com.yz.mall.serial.service.ExtendSerialService;
import com.yz.mall.sys.service.ExtendSysAreaService;
// import com.yz.mall.sys.service.InternalSysFilesService;
import com.yz.mall.sys.service.ExtendSysUserService;
import com.yz.mall.sys.vo.ExtendQofFileInfoVo;
import com.yz.mall.sys.vo.ExtendSysAreaVo;
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

    private final ExtendPmsStockService extendPmsStockService;

    private final ExtendPmsShopCartService extendPmsShopCartService;

    private final ExtendPmsProductService extendPmsProductService;

    private final ExtendSysUserService extendSysUserService;

    // private final InternalSysFilesService internalSysFilesService;

    private final ExtendSysAreaService internalSysAreaService;

    public OmsOrderServiceImpl(ExtendSerialService extendSerialService
            , OmsOrderRelationProductService omsOrderRelationProductService
            , ExtendPmsStockService extendPmsStockService
            , ExtendPmsShopCartService extendPmsShopCartService
            , ExtendPmsProductService extendPmsProductService
            , ExtendSysUserService extendSysUserService
            // , InternalSysFilesService internalSysFilesService
            , ExtendSysAreaService internalSysAreaService) {
        this.extendSerialService = extendSerialService;
        this.omsOrderRelationProductService = omsOrderRelationProductService;
        this.extendPmsStockService = extendPmsStockService;
        this.extendPmsShopCartService = extendPmsShopCartService;
        this.extendPmsProductService = extendPmsProductService;
        this.extendSysUserService = extendSysUserService;
        // this.internalSysFilesService = internalSysFilesService;
        this.internalSysAreaService = internalSysAreaService;
    }

    @Transactional
    @Override
    public OmsOrderSlimVo generateOrder(ExtendOmsOrderByCartDto dto) {
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
        List<ExtendPmsStockDto> deductStocks = new ArrayList<>();

        // 订单商品信息入库
        List<Long> productIds = dto.getProducts().stream().map(ExtendOmsOrderProductDto::getProductId).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productIds) || productIds.size() != dto.getProducts().size()) {
            throw new BusinessException("商品信息无效，请重新选择商品后下单");
        }
        List<ExtendPmsProductSlimVo> productsInfo = extendPmsProductService.getProductByProductIds(productIds);
        if (CollectionUtils.isEmpty(productsInfo)) {
            throw new BusinessException("商品不存在或已下架");
        }
        Map<Long, ExtendPmsProductSlimVo> productMap = productsInfo.stream().collect(Collectors.toMap(ExtendPmsProductSlimVo::getId, t -> t, (a, b) -> a));

        List<OmsOrderRelationProduct> products = new ArrayList<>();

        // 订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ExtendOmsOrderProductDto product : dto.getProducts()) {
            ExtendPmsProductSlimVo productInfo = productMap.get(product.getProductId());
            if (productInfo == null) {
                throw new BusinessException("商品不存在或已下架：" + product.getProductId());
            }
            // 订单下的商品信息
            OmsOrderRelationProduct relationProduct = new OmsOrderRelationProduct();
            BeanUtils.copyProperties(product, relationProduct);
            relationProduct.setProductQuantity(product.getProductQuantity());
            relationProduct.setOrderId(bo.getId());
            relationProduct.setProductName(productInfo.getProductName());
            relationProduct.setProductPrice(productInfo.getProductPrice());
            relationProduct.setAlbumPics(productInfo.getAlbumPics());
            products.add(relationProduct);

            ExtendPmsStockDto stock = new ExtendPmsStockDto();
            stock.setProductId(product.getProductId());
            // 优先使用真实 skuId；未传时由库存服务按商品解析 SKU
            stock.setSkuId(product.getSkuId() != null ? product.getSkuId() : product.getProductId());
            stock.setQuantity(product.getProductQuantity());
            stock.setRemark("订单扣减库存");
            stock.setOrderId(bo.getId());
            deductStocks.add(stock);

            totalAmount = totalAmount.add(relationProduct.getProductPrice().multiply(BigDecimal.valueOf(product.getProductQuantity())));
        }

        bo.setTotalAmount(totalAmount);
        bo.setPayAmount(totalAmount);

        // 扣除商品库存
        extendPmsStockService.deductBatch(deductStocks);
        // 订单信息入库
        baseMapper.insert(bo);
        // 订单详情信息入库
        omsOrderRelationProductService.saveBatch(products);
        // 清理购物车中下单的商品
        extendPmsShopCartService.removeCartByProductIds(bo.getUserId(), deductStocks);
        return new OmsOrderSlimVo(bo.getId(), bo.getOrderCode());
    }

    @Transactional
    @Override
    public Long add(ExtendOmsOrderDto dto) {
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
        List<ExtendPmsStockDto> deductStocks = new ArrayList<>();

        // 订单商品信息入库
        List<OmsOrderRelationProduct> products = new ArrayList<>();
        for (ExtendOmsOrderProductDto product : dto.getProducts()) {
            OmsOrderRelationProduct relationProduct = new OmsOrderRelationProduct();
            BeanUtils.copyProperties(product, relationProduct);
            relationProduct.setOrderId(bo.getId());
            products.add(relationProduct);

            ExtendPmsStockDto stock = new ExtendPmsStockDto();
            stock.setProductId(product.getProductId());
            stock.setSkuId(product.getSkuId() != null ? product.getSkuId() : product.getProductId());
            stock.setQuantity(product.getProductQuantity());
            stock.setRemark("订单扣减库存");
            stock.setOrderId(bo.getId());
            deductStocks.add(stock);
        }

        // 扣除商品库存
        extendPmsStockService.deductBatch(deductStocks);
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
        fillReceiverAreaNames(detailVo);

        // 查询订单商品信息
        List<OmsOrderProductVo> products = omsOrderRelationProductService.getOrderProductsByOrderId(omsOrder.getId());
        detailVo.setProducts(products);

        List<Long> fileIds = new ArrayList<>();
        for (OmsOrderProductVo product : products) {
            if (!StringUtils.hasText(product.getAlbumPics())) {
                continue;
            }
            fileIds.addAll(Arrays.stream(product.getAlbumPics().split(",")).map(Long::parseLong).toList());
        }

        // 获取商品文件信息
        // List<InternalQofFileInfoVo> filesInfo = internalSysFilesService.getFileInfoByFileIdsAndPublic(fileIds);
        List<ExtendQofFileInfoVo> filesInfo = new ArrayList<>();
        if (CollectionUtils.isEmpty(filesInfo)) {
            return detailVo;
        }

        for (OmsOrderProductVo product : detailVo.getProducts()) {
            assembleProductImage(product, filesInfo);
        }
        return detailVo;
    }

    /**
     * 填充收货省市区名称，地区不存在时跳过，避免空指针。
     */
    private void fillReceiverAreaNames(OmsOrderDetailVo detailVo) {
        if (StringUtils.hasText(detailVo.getReceiverProvince())) {
            ExtendSysAreaVo province = internalSysAreaService.getById(detailVo.getReceiverProvince());
            if (province != null) {
                detailVo.setReceiverProvinceName(province.getName());
            }
        }
        if (StringUtils.hasText(detailVo.getReceiverCity())) {
            ExtendSysAreaVo city = internalSysAreaService.getById(detailVo.getReceiverCity());
            if (city != null) {
                detailVo.setReceiverCityName(city.getName());
            }
        }
        if (StringUtils.hasText(detailVo.getReceiverDistrict())) {
            ExtendSysAreaVo district = internalSysAreaService.getById(detailVo.getReceiverDistrict());
            if (district != null) {
                detailVo.setReceiverDistrictName(district.getName());
            }
        }
    }

    /**
     * 组装订单关联的产品的图片预览地址
     *
     * @param vo                    订单里产品信息
     * @param qofFileInfoVos        所有图片预览数据
     */
    private void assembleProductImage(OmsOrderProductVo vo
            , List<ExtendQofFileInfoVo> qofFileInfoVos) {
        if (!StringUtils.hasText(vo.getAlbumPics())) {
            return;
        }
        for (ExtendQofFileInfoVo fileInfoVo : qofFileInfoVos) {
            if (StringUtils.hasText(vo.getPreviewAddress())
                    || !fileInfoVo.getFileId().equals(Long.parseLong(vo.getAlbumPics().split(",")[0]))) {
                continue;
            }
            vo.setPreviewAddress(fileInfoVo.getPreviewAddress());
        }
    }

    @Transactional(rollbackFor = Exception.class)
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
        if (!OmsOrderStatusEnum.PENDING_PAYMENT.getStatus().equals(order.getOrderStatus())) {
            throw new BusinessException("仅待付款订单可取消");
        }
        if (order.getPayType() != null && order.getPayType() != 0) {
            throw new BusinessException("订单已支付，请走退款流程");
        }

        int updated = baseMapper.update(null, new LambdaUpdateWrapper<OmsOrder>()
                .eq(OmsOrder::getId, order.getId())
                .eq(OmsOrder::getOrderStatus, OmsOrderStatusEnum.PENDING_PAYMENT.getStatus())
                .set(OmsOrder::getOrderStatus, OmsOrderStatusEnum.ORDER_CLOSED.getStatus())
                .set(OmsOrder::getUpdateId, userId)
                .set(OmsOrder::getUpdateTime, LocalDateTime.now()));
        if (updated <= 0) {
            throw new BusinessException("订单状态已变更，请刷新后重试");
        }

        // 下单时已扣库存，取消后回补
        restoreStock(order.getId());
        return true;
    }

    /**
     * 按订单商品行回补库存（订单行仅有 productId，由库存服务解析真实 SKU）
     */
    private void restoreStock(Long orderId) {
        List<OmsOrderRelationProduct> products = omsOrderRelationProductService.list(
                new LambdaQueryWrapper<OmsOrderRelationProduct>().eq(OmsOrderRelationProduct::getOrderId, orderId));
        if (CollectionUtils.isEmpty(products)) {
            return;
        }
        for (OmsOrderRelationProduct product : products) {
            if (product.getProductId() == null || product.getProductQuantity() == null || product.getProductQuantity() <= 0) {
                continue;
            }
            ExtendPmsStockDto stockDto = new ExtendPmsStockDto();
            stockDto.setOrderId(orderId);
            stockDto.setProductId(product.getProductId());
            stockDto.setSkuId(product.getProductId());
            stockDto.setQuantity(product.getProductQuantity());
            stockDto.setRemark("取消订单回补库存，订单id=" + orderId);
            Boolean added = extendPmsStockService.add(stockDto);
            if (!Boolean.TRUE.equals(added)) {
                throw new BusinessException("回补库存失败，商品id=" + product.getProductId());
            }
        }
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
        order.setPayTime(LocalDateTime.now());
        order.setUpdateId(userId);
        order.setUpdateTime(LocalDateTime.now());

        // 扣减用户余额
        extendSysUserService.deduct(userId, order.getPayAmount());
        return baseMapper.updateById(order) > 0;
    }

}

