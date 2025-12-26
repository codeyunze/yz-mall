package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.sys.dto.SysLoginLogQueryDto;
import com.yz.mall.sys.entity.SysLoginLog;
import com.yz.mall.sys.vo.SysLoginLogVo;

/**
 * 系统-登录日志(SysLoginLog)表服务接口
 *
 * @author yunze
 * @since 2025-12-11
 */
public interface SysLoginLogService extends IService<SysLoginLog> {

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysLoginLogVo> page(PageFilter<SysLoginLogQueryDto> filter);

    /**
     * 记录登录日志
     *
     * @param username     用户名
     * @param loginIp      登录IP
     * @param loginLocation 登录地点
     * @param os           操作系统
     * @param browser      浏览器类型
     * @param status       登录状态：0-失败，1-成功
     * @param loginType    登录行为：1-账号登录，2-手机号登录，3-第三方登录
     */
    void recordLoginLog(String username, String loginIp, String loginLocation, String os, String browser, Integer status, Integer loginType);

    /**
     * 清空日志
     *
     * @return 是否操作成功
     */
    boolean clearLogs();
}

