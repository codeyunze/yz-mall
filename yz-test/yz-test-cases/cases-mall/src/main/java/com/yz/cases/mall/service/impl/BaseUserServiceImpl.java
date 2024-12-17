package com.yz.cases.mall.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.cases.mall.dto.BaseUserAddDto;
import com.yz.cases.mall.dto.BaseUserQueryDto;
import com.yz.cases.mall.dto.BaseUserUpdateDto;
import com.yz.cases.mall.entity.BaseUser;
import com.yz.cases.mall.mapper.BaseUserMapper;
import com.yz.cases.mall.service.BaseUserService;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 基础-用户(BaseUser)表服务实现类
 *
 * @author yunze
 * @since 2024-06-11 23:16:13
 */
@Service
public class BaseUserServiceImpl extends ServiceImpl<BaseUserMapper, BaseUser> implements BaseUserService {

    @Override
    public Long save(BaseUserAddDto dto) {
        BaseUser bo = new BaseUser();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(BaseUserUpdateDto dto) {
        BaseUser bo = new BaseUser();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<BaseUser> page(PageFilter<BaseUserQueryDto> filter) {
        LambdaQueryWrapper<BaseUser> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

