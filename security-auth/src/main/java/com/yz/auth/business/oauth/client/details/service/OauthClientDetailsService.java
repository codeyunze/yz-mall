package com.yz.auth.business.oauth.client.details.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.auth.business.oauth.client.details.entity.OauthClientDetails;

import java.util.List;

/**
 * 核心表-客户端账号密码、授权、回调地址等重要信息(OauthClientDetails)表服务接口
 *
 * @author yunze
 * @since 2023-02-21 22:51:45
 */
public interface OauthClientDetailsService extends IService<OauthClientDetails> {

    List<OauthClientDetails> getClients();
}

