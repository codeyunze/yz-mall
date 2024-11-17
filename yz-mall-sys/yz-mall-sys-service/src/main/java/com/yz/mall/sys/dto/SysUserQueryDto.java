package com.yz.mall.sys.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础-用户(BaseUser)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Data
public class SysUserQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;


    /**
     * 主键标识
     */
    private String id;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

}

