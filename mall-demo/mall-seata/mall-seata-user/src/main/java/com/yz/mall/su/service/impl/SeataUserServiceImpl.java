package com.yz.mall.su.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.su.dto.SeataUserAddDto;
import com.yz.mall.su.dto.SeataUserQueryDto;
import com.yz.mall.su.dto.SeataUserUpdateDto;
import com.yz.mall.su.mapper.SeataUserMapper;
import com.yz.mall.su.entity.SeataUser;
import com.yz.mall.su.service.SeataUserService;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 分布式事务-用户表(SeataUser)表服务实现类
 *
 * @author yunze
 * @since 2024-06-18 00:00:00
 */
@Service
public class SeataUserServiceImpl extends ServiceImpl<SeataUserMapper, SeataUser> implements SeataUserService {

    @Override
    public Long save(SeataUserAddDto dto) {
        SeataUser bo = new SeataUser();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SeataUserUpdateDto dto) {
        SeataUser bo = new SeataUser();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SeataUser> page(PageFilter<SeataUserQueryDto> filter) {
        LambdaQueryWrapper<SeataUser> queryWrapper = new LambdaQueryWrapper<>();
        SeataUserQueryDto dto = filter.getFilter();
        queryWrapper.eq(dto.getUserId() != null, SeataUser::getUserId, dto.getUserId());
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    /**
     * 扣减用户余额
     */
    @Transactional
    @Override
    public boolean decreaseBalance(Long userId, Long amount) {
        // 先查询用户信息，检查余额是否足够
        LambdaQueryWrapper<SeataUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeataUser::getUserId, userId);
        SeataUser user = baseMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 检查余额是否足够
        if (user.getBalanceCents() < amount) {
            throw new BusinessException("用户余额不足");
        }
        
        // 扣减余额
        // TODO: 2025/11/26 yunze 会有 sql 注入风险
        LambdaUpdateWrapper<SeataUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SeataUser::getUserId, userId)
                    .apply("balance_cents >= {0}", amount)
                    .set(SeataUser::getBalanceCents, user.getBalanceCents() - amount);
        
        int result = baseMapper.update(updateWrapper);
        if (result == 0) {
            throw new BusinessException("余额扣减失败，可能是并发更新导致");
        }
        
        return true;
    }
}