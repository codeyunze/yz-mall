package com.yz.mall.pms.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.pms.dto.PmsProductSlimQueryDto;
import com.yz.mall.pms.enums.ProductPublishStatusEnum;
import com.yz.mall.pms.enums.ProductVerifyStatusEnum;
import com.yz.mall.pms.vo.PmsProductDisplayInfoVo;
import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.sys.service.InternalSysFilesService;
import com.yz.mall.sys.service.InternalSysPendingTasksService;
import com.yz.mall.sys.vo.InternalQofFileInfoVo;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    private final InternalSysFilesService internalSysFilesService;

    public PmsProductServiceImpl(PmsStockService stockService
            , InternalSysPendingTasksService internalSysPendingTasksService
            , InternalSysFilesService internalSysFilesService) {
        this.stockService = stockService;
        this.internalSysPendingTasksService = internalSysPendingTasksService;
        this.internalSysFilesService = internalSysFilesService;
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

    @DS("slave")
    @Override
    public List<PmsProductDisplayInfoVo> getProductDisplayInfoList(PmsProductSlimQueryDto filter) {
        List<PmsProductDisplayInfoVo> displayInfoVos = baseMapper.selectProductDisplayInfoList(filter);
        // 取出所有商品的图片Id Map<图片Id, 产品Id>
        Map<Long, Long> imageIdToProductIdMap = new HashMap<>();
        List<Long> imageIds = new ArrayList<>();
        for (PmsProductDisplayInfoVo displayInfoVo : displayInfoVos) {
            String albumPics = displayInfoVo.getAlbumPics();
            if (!StringUtils.hasText(albumPics)) {
                continue;
            }
            for (String imageId : albumPics.split(",")) {
                imageIdToProductIdMap.put(Long.parseLong(imageId), displayInfoVo.getId());
                imageIds.add(Long.parseLong(imageId));
            }
        }
        // 查询图片的访问信息
        List<InternalQofFileInfoVo> qofFileInfoVos = internalSysFilesService.getFileInfoByFileIdsAndPublic(imageIds);

        // 组装数据，循环遍历产品
        for (PmsProductDisplayInfoVo infoVo : displayInfoVos) {
            if (CollectionUtils.isEmpty(infoVo.getProductImages())) {
                infoVo.setProductImages(new ArrayList<>());
            }
            for (InternalQofFileInfoVo fileInfoVo : qofFileInfoVos) {
                if (!imageIdToProductIdMap.containsKey(fileInfoVo.getFileId())) {
                    continue;
                }
                Long productId = imageIdToProductIdMap.get(fileInfoVo.getFileId());
                if (productId.equals(infoVo.getId())) {
                    infoVo.getProductImages().add(fileInfoVo.getPreviewAddress());
                }
            }
        }
        return displayInfoVos;
    }

    @DS("slave")
    @Override
    public Map<Long, PmsProductDisplayInfoVo> getProductDisplayInfoMap(List<Long> ids) {
        LambdaQueryWrapper<PmsProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PmsProduct::getId, PmsProduct::getName, PmsProduct::getPrice, PmsProduct::getTitles
                , PmsProduct::getRemark, PmsProduct::getAlbumPics, PmsProduct::getPublishStatus);
        // queryWrapper.eq(PmsProduct::getPublishStatus, ProductPublishStatusEnum.PUBLISH.get());
        queryWrapper.eq(PmsProduct::getVerifyStatus, ProductVerifyStatusEnum.APPROVED_REVIEW.get());
        queryWrapper.in(PmsProduct::getId, ids);
        List<PmsProduct> products = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(products)) {
            return Collections.emptyMap();
        }
        // 获取指定商品的库存数量
        Map<Long, Integer> stockByProductId = stockService.getStockByProductIds(ids);
        Map<Long, PmsProductDisplayInfoVo> map = new HashMap<>();
        products.forEach(product -> {
            PmsProductDisplayInfoVo vo = new PmsProductDisplayInfoVo();
            BeanUtils.copyProperties(product, vo);
            vo.setQuantity(stockByProductId.get(product.getId()) != null ? stockByProductId.get(product.getId()) : 0);
            map.put(product.getId(), vo);
        });
        return map;
    }
}