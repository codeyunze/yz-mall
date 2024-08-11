package com.yz.mall.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.user.dto.SysApplicationAddDto;
import com.yz.mall.user.dto.SysApplicationQueryDto;
import com.yz.mall.user.dto.SysApplicationUpdateDto;
import com.yz.mall.user.mapper.SysApplicationMapper;
import com.yz.mall.user.entity.SysApplication;
import com.yz.mall.user.service.SysApplicationService;
import com.yz.mall.user.vo.SysApplicationVo;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 应用配置(SysApplication)表服务实现类
 *
 * @author yunze
 * @since 2024-08-11 20:10:14
 */
@Service
public class SysApplicationServiceImpl extends ServiceImpl<SysApplicationMapper, SysApplication> implements SysApplicationService {

    @Override
    public String save(SysApplicationAddDto dto) {
        SysApplication bo = new SysApplication();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextIdStr());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysApplicationUpdateDto dto) {
        SysApplication bo = new SysApplication();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysApplication> page(PageFilter<SysApplicationQueryDto> filter) {
        LambdaQueryWrapper<SysApplication> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @Override
    public SysApplicationVo getByClientId(@NotBlank(message = "应用客户端Id不能为空") String clientId) {
        LambdaQueryWrapper<SysApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysApplication::getClientId, clientId);
        SysApplication selected = baseMapper.selectOne(queryWrapper);
        SysApplicationVo result = new SysApplicationVo();
        BeanUtils.copyProperties(selected, result);
        return result;
    }
}

