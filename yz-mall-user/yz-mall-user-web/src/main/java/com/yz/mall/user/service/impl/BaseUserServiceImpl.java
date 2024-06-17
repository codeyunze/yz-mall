package com.yz.mall.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.user.dto.BaseUserAddDto;
import com.yz.mall.user.dto.BaseUserQueryDto;
import com.yz.mall.user.dto.BaseUserUpdateDto;
import com.yz.mall.user.mapper.BaseUserMapper;
import com.yz.mall.user.entity.BaseUser;
import com.yz.mall.user.service.BaseUserService;
import com.yz.mall.user.vo.BaseUserVo;
import com.yz.tools.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 基础-用户(BaseUser)表服务实现类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Service
public class BaseUserServiceImpl extends ServiceImpl<BaseUserMapper, BaseUser> implements BaseUserService {

    @Override
    public String save(BaseUserAddDto dto) {
        BaseUser bo = new BaseUser();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextIdStr());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(BaseUserUpdateDto dto) {
        BaseUser bo = new BaseUser();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }


    @DS("slave")
    @Override
    public Page<BaseUser> page(PageFilter<BaseUserQueryDto> filter) {
        LambdaQueryWrapper<BaseUser> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @Override
    public BaseUserVo get(String account) {
        return baseMapper.get(account);
    }
}

