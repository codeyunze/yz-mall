package com.yz.mall.so.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.so.dto.SeataOrderAddDto;
import com.yz.mall.so.dto.SeataOrderQueryDto;
import com.yz.mall.so.dto.SeataOrderUpdateDto;
import com.yz.mall.so.entity.SeataOrder;
import com.yz.mall.so.feign.SeataStockFeign;
import com.yz.mall.so.feign.SeataUserFeign;
import com.yz.mall.so.mapper.SeataOrderMapper;
import com.yz.mall.so.service.SeataOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 分布式事务-订单表(SeataOrder)表服务实现类
 *
 * @author yunze
 * @since 2025-11-24 22:40:03
 */
@Service
public class SeataOrderServiceImpl extends ServiceImpl<SeataOrderMapper, SeataOrder> implements SeataOrderService {

    private final SeataStockFeign stockFeign;

    private final SeataUserFeign userFeign;

    public SeataOrderServiceImpl(SeataStockFeign stockFeign, SeataUserFeign userFeign) {
        this.stockFeign = stockFeign;
        this.userFeign = userFeign;
    }

    @GlobalTransactional(name = "addOrder", rollbackFor = Exception.class)
    @Override
    public Long save(SeataOrderAddDto dto) {
        SeataOrder bo = new SeataOrder();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        if (baseMapper.insert(bo) == 0) {
            throw new BusinessException("订单保存失败");
        }

        Result<Boolean> decreaseStock = stockFeign.decreaseStock(dto.getProductId(), 1);
        if (0 != decreaseStock.getCode()) {
            throw new BusinessException("库存扣减失败");
        }

        Result<Boolean> booleanResult = userFeign.decreaseBalance(dto.getUserId(), dto.getProductPrice());
        if (0 != booleanResult.getCode()) {
            throw new BusinessException("用户余额不足");
        }
        return bo.getId();
    }

    @Override
    public boolean update(SeataOrderUpdateDto dto) {
        SeataOrder bo = new SeataOrder();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SeataOrder> page(PageFilter<SeataOrderQueryDto> filter) {
        LambdaQueryWrapper<SeataOrder> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

