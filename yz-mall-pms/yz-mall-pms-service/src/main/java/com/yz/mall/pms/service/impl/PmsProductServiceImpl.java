package com.yz.mall.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.enums.ProductVerifyStatusEnum;
import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.sys.service.InternalSysPendingTasksService;
import com.yz.mall.web.exception.DataNotExistException;
import com.yz.mall.pms.entity.PmsProduct;
import com.yz.mall.pms.mapper.PmsProductMapper;
import com.yz.mall.pms.service.PmsProductService;
import com.yz.mall.pms.dto.PmsProductAddDto;
import com.yz.mall.pms.dto.PmsProductQueryDto;
import com.yz.mall.pms.dto.PmsProductUpdateDto;
import com.yz.mall.pms.service.PmsStockService;
import com.yz.mall.pms.vo.PmsProductVo;
import com.yz.mall.web.common.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 商品表(PmsProduct)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Service
public class PmsProductServiceImpl extends ServiceImpl<PmsProductMapper, PmsProduct> implements PmsProductService {

    private final PmsStockService stockService;

    private final InternalSysPendingTasksService internalSysPendingTasksService;

    public PmsProductServiceImpl(PmsStockService stockService
            , InternalSysPendingTasksService internalSysPendingTasksService) {
        this.stockService = stockService;
        this.internalSysPendingTasksService = internalSysPendingTasksService;
    }

    @Transactional
    @Override
    public Long save(PmsProductAddDto dto) {
        PmsProduct bo = new PmsProduct();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(PmsProductUpdateDto dto) {
        PmsProduct bo = new PmsProduct();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public boolean publish(Long id) {
        PmsProduct product = baseMapper.selectById(id);
        if (product == null) {
            throw new DataNotExistException("商品信息不存在，无法上架");
        }
        product.setPublishStatus(1);

        int updated = baseMapper.updateById(product);
        InternalSysPendingTasksAddDto tasksAddDto = new InternalSysPendingTasksAddDto();
        tasksAddDto.setTaskCode("PMS:PRODUCT:PUBLISH");
        tasksAddDto.setTaskNode("待审核");
        tasksAddDto.setTaskTitle(product.getTitles() + "上架审核");
        tasksAddDto.setBusinessId(String.valueOf(id));
        Long taskId = internalSysPendingTasksService.startTask(tasksAddDto);
        return updated > 0;
    }

    @Override
    public boolean delisting(Long id) {
        PmsProduct product = baseMapper.selectById(id);
        if (product == null) {
            throw new DataNotExistException("商品信息不存在，无法下架");
        }
        product.setPublishStatus(0);
        return baseMapper.updateById(product) > 0;
    }

    @Override
    public Page<PmsProduct> page(PageFilter<PmsProductQueryDto> filter) {
        PmsProductQueryDto query = filter.getFilter();
        LambdaQueryWrapper<PmsProduct> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.hasText(query.getName()), PmsProduct::getName, query.getName());
        queryWrapper.like(StringUtils.hasText(query.getTitles()), PmsProduct::getTitles, query.getTitles());
        queryWrapper.eq(query.getVerifyStatus() != null, PmsProduct::getVerifyStatus, query.getVerifyStatus());
        queryWrapper.eq(query.getPublishStatus() != null, PmsProduct::getPublishStatus, query.getPublishStatus());

        queryWrapper.orderByDesc(PmsProduct::getId);
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @DS("slave")
    @Override
    public PmsProductVo detail(Long id) {
        PmsProduct bo = baseMapper.selectById(id);
        PmsProductVo product = new PmsProductVo();
        BeanUtils.copyProperties(bo, product);

        product.setQuantity(stockService.getStockByProductId(id));
        return product;
    }

    @Override
    public void pendingReview(Long id) {
        PmsProduct product = baseMapper.selectById(id);
        product.setVerifyStatus(ProductVerifyStatusEnum.PENDING_REVIEW.get());
        baseMapper.updateById(product);
    }

    @Override
    public void approvedReview(Long id) {
        PmsProduct product = baseMapper.selectById(id);
        product.setVerifyStatus(ProductVerifyStatusEnum.APPROVED_REVIEW.get());
        baseMapper.updateById(product);
    }
}