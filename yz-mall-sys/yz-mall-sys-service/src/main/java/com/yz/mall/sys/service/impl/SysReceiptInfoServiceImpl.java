package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysReceiptInfoAddDto;
import com.yz.mall.sys.dto.SysReceiptInfoQueryDto;
import com.yz.mall.sys.dto.SysReceiptInfoUpdateDto;
import com.yz.mall.sys.entity.SysReceiptInfo;
import com.yz.mall.sys.mapper.SysReceiptInfoMapper;
import com.yz.mall.sys.service.SysReceiptInfoService;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.exception.AuthenticationException;
import com.yz.mall.web.exception.DataNotExistException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 系统-用户收货信息(SysReceiptInfo)表服务实现类
 *
 * @author yunze
 * @since 2025-03-03 15:40:27
 */
@Service
public class SysReceiptInfoServiceImpl extends ServiceImpl<SysReceiptInfoMapper, SysReceiptInfo> implements SysReceiptInfoService {

    @Override
    public Long save(SysReceiptInfoAddDto dto) {
        SysReceiptInfo bo = new SysReceiptInfo();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysReceiptInfoUpdateDto dto) {
        SysReceiptInfo receiptInfo = baseMapper.selectById(dto.getId());
        if (receiptInfo == null) {
            throw new DataNotExistException();
        }
        if (!receiptInfo.getCreateId().equals(dto.getCreateId())) {
            throw new AuthenticationException();
        }

        SysReceiptInfo bo = new SysReceiptInfo();
        BeanUtils.copyProperties(dto, bo);
        bo.setUpdateTime(LocalDateTime.now());
        LambdaUpdateWrapper<SysReceiptInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysReceiptInfo::getId, bo.getId());
        updateWrapper.eq(SysReceiptInfo::getCreateId, dto.getCreateId());
        return baseMapper.update(bo, updateWrapper) > 0;
    }

    @Override
    public boolean removeById(Long id, Long userId) {
        LambdaUpdateWrapper<SysReceiptInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysReceiptInfo::getId, id);
        updateWrapper.eq(SysReceiptInfo::getCreateId, userId);
        return baseMapper.delete(updateWrapper) > 0;
    }

    @DS("slave")
    @Override
    public Page<SysReceiptInfo> page(PageFilter<SysReceiptInfoQueryDto> filter) {
        LambdaQueryWrapper<SysReceiptInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysReceiptInfo::getCreateId, filter.getFilter().getCreateId());
        queryWrapper.like(StringUtils.hasText(filter.getFilter().getReceiverName()), SysReceiptInfo::getReceiverName, filter.getFilter().getReceiverName());
        queryWrapper.eq(StringUtils.hasText(filter.getFilter().getReceiverPhone()), SysReceiptInfo::getReceiverPhone, filter.getFilter().getReceiverPhone());
        queryWrapper.orderByDesc(SysReceiptInfo::getUpdateTime);
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

