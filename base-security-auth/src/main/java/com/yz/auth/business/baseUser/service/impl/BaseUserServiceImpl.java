package com.yz.auth.business.baseUser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.auth.business.baseUser.entity.BaseUser;
import com.yz.auth.business.baseUser.mapper.BaseUserMapper;
import com.yz.auth.business.baseUser.service.BaseUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 基础-用户表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2022-09-13
 */
@Service
public class BaseUserServiceImpl extends ServiceImpl<BaseUserMapper, BaseUser> implements BaseUserService {

    @Override
    public BaseUser findOneByUserAccount(String userAccount) {
        return baseMapper.findOneByUserAccount(userAccount);
    }
}
