package com.yz.auth.business.user.entity;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户表(TbUser)表实体类
 *
 * @author makejava
 * @since 2023-02-14 23:35:56
 */
@Data
@SuppressWarnings("serial")
public class TbUser extends Model<TbUser> {

    private Long id;
    // 用户名
    private String username;
    // 密码，加密存储
    private String password;
    // 注册手机号
    private String phone;
    // 注册邮箱
    private String email;

    private Date created;

    private Date updated;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}

