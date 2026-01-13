package com.yz.mall.sys.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 在线用户查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-12-11
 */
@Data
public class SysOnlineUserQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;
}

