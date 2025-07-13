package com.yz.mall.auth.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.SaLoginModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Sa-Token的全局侦听器
 *
 * @author yunze
 * @date 2024/8/6 星期二 22:44
 */
@Slf4j
@Component
public class YzManSaTokenListener implements SaTokenListener {

    /**
     * 每次登录时触发
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        log.info("---------- 自定义侦听器实现 doLogin: {}", loginId);
    }

    /**
     * 每次注销时触发
     */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        log.info("---------- 自定义侦听器实现 doLogout: {}", loginId);
    }

    /**
     * 每次被踢下线时触发
     */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        log.info("---------- 自定义侦听器实现 doKickout: {}", loginId);
    }

    /**
     * 每次被顶下线时触发
     */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        log.info("---------- 自定义侦听器实现 doReplaced: {}", loginId);
    }

    /**
     * 每次被封禁时触发
     */
    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
        log.info("---------- 自定义侦听器实现 doDisable: {}", loginId);
    }

    /**
     * 每次被解封时触发
     */
    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
        log.info("---------- 自定义侦听器实现 doUntieDisable: {}", loginId);
    }

    /**
     * 每次二级认证时触发
     */
    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
        log.info("---------- 自定义侦听器实现 doOpenSafe: {}", tokenValue);
    }

    /**
     * 每次退出二级认证时触发
     */
    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {
        log.info("---------- 自定义侦听器实现 doCloseSafe: {}", tokenValue);
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCreateSession(String id) {
        log.info("---------- 自定义侦听器实现 doCreateSession: {}", id);
    }

    /**
     * 每次注销Session时触发
     */
    @Override
    public void doLogoutSession(String id) {
        log.info("---------- 自定义侦听器实现 doLogoutSession: {}", id);
    }

    /**
     * 每次Token续期时触发
     */
    @Override
    public void doRenewTimeout(String tokenValue, Object loginId, long timeout) {
        log.info("---------- 自定义侦听器实现 doRenewTimeout: {}", loginId);
    }

}
