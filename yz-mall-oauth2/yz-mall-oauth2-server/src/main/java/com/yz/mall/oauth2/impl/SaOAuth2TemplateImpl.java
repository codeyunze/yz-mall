package com.yz.mall.oauth2.impl;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import com.yz.mall.user.service.BaseUserService;
import com.yz.mall.user.service.SysApplicationService;
import com.yz.mall.user.vo.SysApplicationVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yunze
 * @date 2024/8/6 星期二 23:38
 */
@Slf4j
@Component
public class SaOAuth2TemplateImpl extends SaOAuth2Template {

    @Autowired
    private SysApplicationService sysApplicationService;

    /**
     * 根据 id 获取 Client 信息
     * @param clientId 应用客户端Id
     * @return
     */
    @Override
    public SaClientModel getClientModel(String clientId) {
        log.info("根据 clientId 获取 Client 的信息");
        // 此为模拟数据，真实环境需要从数据库查询
        SysApplicationVo vo = sysApplicationService.getByClientId(clientId);
        if (vo == null) {
            return null;
        }

        return new SaClientModel()
                .setClientId(vo.getClientId())
                .setClientSecret(vo.getClientSecret())
                .setAllowUrl("*")
                .setContractScope("userinfo")
                .setIsAutoMode(true);
    }

    // 根据ClientId 和 LoginId 获取openid
    @Override
    public String getOpenid(String clientId, Object loginId) {
        log.info("根据ClientId 和 LoginId 获取openid");
        // 此为模拟数据，真实环境需要从数据库查询
        return "gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__";
    }

    // -------------- 其它需要重写的函数
}
