package com.yz.mall.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户基础信息
 * @author yunze
 * @date 2026/1/17 星期六 14:47
 */
@Data
public class ExtendSysUserSlimVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户名
     */
    private String username;

}
