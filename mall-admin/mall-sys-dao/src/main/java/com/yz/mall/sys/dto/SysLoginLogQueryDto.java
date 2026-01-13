package com.yz.mall.sys.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-12-11
 */
@Data
public class SysLoginLogQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 登录开始时间
     */
    private LocalDateTime startTime;

    /**
     * 登录结束时间
     */
    private LocalDateTime endTime;
}

