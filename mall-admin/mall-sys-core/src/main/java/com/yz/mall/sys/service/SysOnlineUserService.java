package com.yz.mall.sys.service;

import com.yz.mall.base.PageFilter;
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
     * @param filter 查询条件
     * @return 在线用户列表
     */
    List<SysOnlineUserVo> list(PageFilter<SysOnlineUserQueryDto> filter);

    /**
     * 踢下线指定用户
     *
     * @param token 用户 token
     * @return 是否操作成功
     */
    boolean kickout(String token);

    /**
     * 踢下线指定用户 ID 的所有会话
     *
     * @param userId 用户 ID
     * @return 是否操作成功
     */
    boolean kickoutByUserId(Long userId);
}

