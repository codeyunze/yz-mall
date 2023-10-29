package com.yz.auth.business.oauth.client.details.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.auth.business.oauth.client.details.dao.OauthClientDetailsDao;
import com.yz.auth.business.oauth.client.details.entity.OauthClientDetails;
import com.yz.auth.business.oauth.client.details.service.OauthClientDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 核心表-客户端账号密码、授权、回调地址等重要信息(OauthClientDetails)表服务实现类
 *
 * @author yunze
 * @since 2023-02-21 22:51:45
 */
@Service("oauthClientDetailsService")
public class OauthClientDetailsServiceImpl extends ServiceImpl<OauthClientDetailsDao, OauthClientDetails> implements OauthClientDetailsService {

    @Override
    public List<OauthClientDetails> getClients() {
        LambdaQueryWrapper<OauthClientDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OauthClientDetails::getScope, "all").orderByDesc(OauthClientDetails::getClientId);
        return baseMapper.selectList(queryWrapper);
    }
}

