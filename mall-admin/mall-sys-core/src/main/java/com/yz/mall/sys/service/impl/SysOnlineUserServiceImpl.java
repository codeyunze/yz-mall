package com.yz.mall.sys.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yz.mall.base.PageFilter;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.sys.dto.SysOnlineUserQueryDto;
import com.yz.mall.sys.entity.SysLoginLog;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.mapper.SysLoginLogMapper;
import com.yz.mall.sys.mapper.SysUserMapper;
import com.yz.mall.sys.service.SysOnlineUserService;
import com.yz.mall.sys.vo.SysOnlineUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在线用户服务实现类
 *
 * @author yunze
 * @since 2025-12-11
 */
@Slf4j
@Service
public class SysOnlineUserServiceImpl implements SysOnlineUserService {

    private final SysUserMapper sysUserMapper;
    private final SysLoginLogMapper sysLoginLogMapper;
    private final RedisTemplate<String, Object> defaultRedisTemplate;

    public SysOnlineUserServiceImpl(SysUserMapper sysUserMapper,
                                    SysLoginLogMapper sysLoginLogMapper,
                                    RedisTemplate<String, Object> defaultRedisTemplate) {
        this.sysUserMapper = sysUserMapper;
        this.sysLoginLogMapper = sysLoginLogMapper;
        this.defaultRedisTemplate = defaultRedisTemplate;
    }

    @Override
    public List<SysOnlineUserVo> list(PageFilter<SysOnlineUserQueryDto> filter) {
        List<SysOnlineUserVo> result = new ArrayList<>();

        // 获取所有已登录的会话 id
        List<String> sessionIdList = StpUtil.searchSessionId("", (int) ((filter.getCurrent() < 0 ? 0 : filter.getCurrent() - 1) * filter.getSize()), (int) filter.getSize(), false);

        List<Long> userIds = new ArrayList<>();
        for (String sessionId : sessionIdList) {
            // 根据会话id，查询对应的 SaSession 对象，此处一个 SaSession 对象即代表一个登录的账号
            SaSession session = StpUtil.getSessionBySessionId(sessionId);

            // 查询这个账号都在哪些设备登录了，依据上面的示例，账号A 的 SaTerminalInfo 数量是 3，账号B 的 SaTerminalInfo 数量是 2
            List<SaTerminalInfo> terminalList = session.terminalListCopy();
            userIds.add(Long.parseLong(session.getLoginId().toString()));
            System.out.println("用户Id：" + session.getLoginId() + "，会话id：" + sessionId + "，共在 " + terminalList.size() + " 设备登录");
        }

        try {
            // 获取所有在线用户的token列表
            // List<String> tokenList = StpUtil.getTokenValueList(0, -1);

            for (Long userId : userIds) {
                try {

                    // 获取该用户最新的登录日志信息
                    SysLoginLog latestLog = getLatestLoginLog(userId);
                    
                    SysOnlineUserVo vo = new SysOnlineUserVo();
                    vo.setUserId(userId);
                    vo.setUsername(latestLog.getUsername());
                    
                    if (latestLog != null) {
                        vo.setLoginIp(latestLog.getLoginIp());
                        vo.setLoginLocation(latestLog.getLoginLocation());
                        vo.setOs(latestLog.getOs());
                        vo.setBrowser(latestLog.getBrowser());
                        vo.setLoginTime(latestLog.getLoginTime());
                    } else {
                        // 如果没有登录日志，设置默认值
                        vo.setLoginIp("未知");
                        vo.setLoginLocation("未知");
                        vo.setOs("未知");
                        vo.setBrowser("未知");
                        vo.setLoginTime(LocalDateTime.now());
                    }

                    result.add(vo);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("获取在线用户列表失败", e);
        }

        return result;
    }

    @Override
    public boolean kickout(String token) {
        try {
            // 获取token对应的用户ID
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                return false;
            }

            Long userId = Long.parseLong(loginId.toString());

            // 清理角色缓存信息
            defaultRedisTemplate.delete(RedisCacheKey.permissionRole(token));

            // 清理用户信息
            Object refreshToken = defaultRedisTemplate.boundHashOps(RedisCacheKey.loginInfo(userId)).get("refreshToken");
            if (refreshToken != null) {
                defaultRedisTemplate.delete(RedisCacheKey.loginInfo(userId));
            }

            // 踢下线
            StpUtil.kickout(loginId);
            return true;
        } catch (Exception e) {
            log.error("踢下线用户失败, token: {}", token, e);
            return false;
        }
    }

    @Override
    public boolean kickoutByUserId(Long userId) {
        try {
            // 清理角色缓存信息
            // List<String> tokenList = StpUtil.getTokenValueListByLoginId(userId.toString(), 0, -1);
            List<String> tokenList = new ArrayList<>();
            for (String token : tokenList) {
                defaultRedisTemplate.delete(RedisCacheKey.permissionRole(token));
            }

            // 清理用户信息
            Object refreshToken = defaultRedisTemplate.boundHashOps(RedisCacheKey.loginInfo(userId)).get("refreshToken");
            if (refreshToken != null) {
                defaultRedisTemplate.delete(RedisCacheKey.loginInfo(userId));
            }

            // 踢下线该用户的所有会话
            StpUtil.kickout(userId.toString());
            return true;
        } catch (Exception e) {
            log.error("踢下线用户失败, userId: {}", userId, e);
            return false;
        }
    }

    /**
     * 获取用户最新的登录日志
     *
     * @param userId 用户 Id
     * @return 最新的登录日志
     */
    private SysLoginLog getLatestLoginLog(Long userId) {
        if (userId == null) {
            return null;
        }

        try {
            LambdaQueryWrapper<SysLoginLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysLoginLog::getUserId, userId)
                    .eq(SysLoginLog::getStatus, 1) // 只查询成功的登录记录
                    .eq(SysLoginLog::getInvalid, 0)
                    .orderByDesc(SysLoginLog::getLoginTime)
                    .last("LIMIT 1");
            return sysLoginLogMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.warn("获取用户 {} 的最新登录日志失败: {}", userId, e.getMessage());
            return null;
        }
    }
}

