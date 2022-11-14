package com.yz.auth.baseClient.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gh.auth.baseClient.entity.BaseClient;

/**
 * <p>
 * 基础-客户端表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2022-10-07
 */
public interface BaseClientService extends IService<BaseClient> {

    BaseClient getClientByClientId(String clientId);

}
