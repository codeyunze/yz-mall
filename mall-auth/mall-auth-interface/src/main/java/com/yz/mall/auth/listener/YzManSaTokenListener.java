package com.yz.mall.auth.listener;

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
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
    @Override
    public void doLogin(String s, Object o, String s1, SaLoginParameter saLoginParameter) {

    }

    @Override
    public void doLogout(String s, Object o, String s1) {

    }

    @Override
    public void doKickout(String s, Object o, String s1) {

    }

    @Override
    public void doReplaced(String s, Object o, String s1) {

    }

    @Override
    public void doDisable(String s, Object o, String s1, int i, long l) {

    }

    @Override
    public void doUntieDisable(String s, Object o, String s1) {

    }

    @Override
    public void doOpenSafe(String s, String s1, String s2, long l) {

    }

    @Override
    public void doCloseSafe(String s, String s1, String s2) {

    }

    @Override
    public void doCreateSession(String s) {

    }

    @Override
    public void doLogoutSession(String s) {

    }

    @Override
    public void doRenewTimeout(String s, Object o, String s1, long l) {

    }
    //
    // /**
    //  * 每次登录时触发
    //  *
    //  * @param loginType      账号类别
    //  * @param loginId        账号id
    //  * @param tokenValue     本次登录产生的 token 值
    //  * @param loginParameter 登录参数
    //  */
    // @Override
    // public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
    //     log.info("---------- 自定义侦听器实现 doLogin: {}", loginParameter.toString());
    // }
    //
    // /**
    //  * 每次注销时触发
    //  */
    // @Override
    // public void doLogout(String loginType, Object loginId, String tokenValue) {
    //     log.info("---------- 自定义侦听器实现 doLogout: {}", loginId);
    // }
    //
    // /**
    //  * 每次被踢下线时触发
    //  */
    // @Override
    // public void doKickout(String loginType, Object loginId, String tokenValue) {
    //     log.info("---------- 自定义侦听器实现 doKickout: {}", loginId);
    // }
    //
    // /**
    //  * 每次被顶下线时触发
    //  */
    // @Override
    // public void doReplaced(String loginType, Object loginId, String tokenValue) {
    //     log.info("---------- 自定义侦听器实现 doReplaced: {}", loginId);
    // }
    //
    // /**
    //  * 每次被封禁时触发
    //  */
    // @Override
    // public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
    //     log.info("---------- 自定义侦听器实现 doDisable: {}", loginId);
    // }
    //
    // /**
    //  * 每次被解封时触发
    //  */
    // @Override
    // public void doUntieDisable(String loginType, Object loginId, String service) {
    //     log.info("---------- 自定义侦听器实现 doUntieDisable: {}", loginId);
    // }
    //
    // /**
    //  * 每次二级认证时触发
    //  */
    // @Override
    // public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
    //     log.info("---------- 自定义侦听器实现 doOpenSafe: {}", tokenValue);
    // }
    //
    // /**
    //  * 每次退出二级认证时触发
    //  */
    // @Override
    // public void doCloseSafe(String loginType, String tokenValue, String service) {
    //     log.info("---------- 自定义侦听器实现 doCloseSafe: {}", tokenValue);
    // }
    //
    // /**
    //  * 每次创建Session时触发
    //  */
    // @Override
    // public void doCreateSession(String id) {
    //     log.info("---------- 自定义侦听器实现 doCreateSession: {}", id);
    // }
    //
    // /**
    //  * 每次注销Session时触发
    //  */
    // @Override
    // public void doLogoutSession(String id) {
    //     log.info("---------- 自定义侦听器实现 doLogoutSession: {}", id);
    // }
    //
    // @Override
    // public void doRegisterComponent(String compName, Object compObj) {
    //     SaTokenListener.super.doRegisterComponent(compName, compObj);
    // }
    //
    // @Override
    // public void doRegisterAnnotationHandler(SaAnnotationHandlerInterface<?> handler) {
    //     SaTokenListener.super.doRegisterAnnotationHandler(handler);
    // }
    //
    // @Override
    // public void doSetStpLogic(StpLogic stpLogic) {
    //     SaTokenListener.super.doSetStpLogic(stpLogic);
    // }
    //
    // @Override
    // public void doSetConfig(SaTokenConfig config) {
    //     SaTokenListener.super.doSetConfig(config);
    // }

}
