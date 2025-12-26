package com.yz.mall.sys.service;

import com.yz.mall.sys.dto.SysOnlineUserQueryDto;
import com.yz.mall.sys.vo.SysOnlineUserVo;

import java.util.List;

/**
 * 在线用户服务接口
 *
 * @author yunze
 * @since 2025-12-11
 */
public interface SysOnlineUserService {

    /**
     * 获取在线用户列表
     *
     * @param queryDto 查询条件
     * @return 在线用户列表
     */
    List<SysOnlineUserVo> list(SysOnlineUserQueryDto queryDto);

    /**
     * 踢下线指定用户
     *
     * @param token 用户token
     * @return 是否操作成功
     */
    boolean kickout(String token);

    /**
     * 踢下线指定用户ID的所有会话
     *
     * @param userId 用户ID
     * @return 是否操作成功
     */
    boolean kickoutByUserId(Long userId);
}

