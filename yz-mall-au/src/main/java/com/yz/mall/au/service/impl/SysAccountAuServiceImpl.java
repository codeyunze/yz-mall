package com.yz.mall.au.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.au.dto.SysAccountAuAddDto;
import com.yz.mall.au.dto.SysAccountAuQueryDto;
import com.yz.mall.au.dto.SysAccountAuUpdateDto;
import com.yz.mall.au.mapper.SysAccountAuMapper;
import com.yz.mall.au.entity.SysAccountAu;
import com.yz.mall.au.service.SysAccountAuService;
import com.yz.mall.au.vo.SysAccountAuVo;
import com.yz.mall.web.common.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 个人黄金账户(SysAccountAu)表服务实现类
 *
 * @author yunze
 * @since 2025-01-05 10:06:32
 */
@Service
public class SysAccountAuServiceImpl extends ServiceImpl<SysAccountAuMapper, SysAccountAu> implements SysAccountAuService {

    @Override
    public Long save(SysAccountAuAddDto dto) {
        SysAccountAu bo = new SysAccountAu();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setUserId(Long.parseLong(StpUtil.getLoginId().toString()));
        if (1 == dto.getTransactionType()) {
            // 计算盈利金额
            SysAccountAu purchase = baseMapper.selectById(dto.getRelationId());
            // 卖出价格 - 买入价格 - 2.5元手续费
            bo.setProfitAmount(bo.getPrice().subtract(purchase.getPrice()).subtract(BigDecimal.valueOf(2.5)));
        }
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysAccountAuUpdateDto dto) {
        SysAccountAu bo = new SysAccountAu();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysAccountAuVo> getPageByFilter(PageFilter<SysAccountAuQueryDto> filter) {
        return baseMapper.selectPageByFilter(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Long id) {
        SysAccountAu record = baseMapper.selectById(id);
        if (1 == record.getTransactionType()) {
            // 清理卖出记录
            return baseMapper.deleteById(id) > 0;
        }

        // 调整关联这一条买入记录的卖出记录
        LambdaQueryWrapper<SysAccountAu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysAccountAu::getRelationId, id);
        // 卖出记录
        List<SysAccountAu> list = baseMapper.selectList(queryWrapper);
        list.forEach(item -> {
            item.setProfitAmount(BigDecimal.ZERO);
            item.setRelationId(-1L);
        });
        super.updateBatchById(list);
        return baseMapper.deleteById(id) > 0;
    }
}

