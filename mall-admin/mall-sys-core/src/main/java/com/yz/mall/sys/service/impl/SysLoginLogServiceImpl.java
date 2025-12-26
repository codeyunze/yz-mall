package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.sys.dto.SysLoginLogQueryDto;
import com.yz.mall.sys.entity.SysLoginLog;
import com.yz.mall.sys.mapper.SysLoginLogMapper;
import com.yz.mall.sys.service.SysLoginLogService;
import com.yz.mall.sys.vo.SysLoginLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 系统-登录日志(SysLoginLog)表服务实现类
 *
 * @author yunze
 * @since 2025-12-11
 */
@Slf4j
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    @Override
    public Page<SysLoginLogVo> page(PageFilter<SysLoginLogQueryDto> filter) {
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), filter.getFilter());
    }

    @Override
    public void recordLoginLog(String username, String loginIp, String loginLocation, String os, String browser, Integer status, Integer loginType) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setId(IdUtil.getSnowflakeNextId());
            loginLog.setUsername(username);
            loginLog.setLoginIp(loginIp);
            loginLog.setLoginLocation(loginLocation);
            loginLog.setOs(os);
            loginLog.setBrowser(browser);
            loginLog.setStatus(status);
            loginLog.setLoginType(loginType);
            loginLog.setLoginTime(LocalDateTime.now());
            loginLog.setCreateTime(LocalDateTime.now());
            loginLog.setUpdateTime(LocalDateTime.now());
            baseMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }

    @Override
    public boolean clearLogs() {
        LambdaQueryWrapper<SysLoginLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysLoginLog::getInvalid, 0);
        return baseMapper.delete(queryWrapper) > 0;
    }
}

