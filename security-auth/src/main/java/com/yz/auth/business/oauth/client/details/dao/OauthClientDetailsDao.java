package com.yz.auth.business.oauth.client.details.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.auth.business.oauth.client.details.entity.OauthClientDetails;

/**
 * 核心表-客户端账号密码、授权、回调地址等重要信息(OauthClientDetails)表数据库访问层
 *
 * @author yunze
 * @since 2023-02-21 22:51:45
 */
public interface OauthClientDetailsDao extends BaseMapper<OauthClientDetails> {

}

