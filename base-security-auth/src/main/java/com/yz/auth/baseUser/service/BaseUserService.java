package com.yz.auth.baseUser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.auth.baseUser.entity.BaseUser;

/**
 * <p>
 * 基础-用户表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2022-09-13
 */
public interface BaseUserService extends IService<BaseUser> {

    BaseUser findOneByUserAccount(String userAccount);
}
