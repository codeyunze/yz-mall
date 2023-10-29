package com.yz.auth.business.user.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 用户角色表(TbUserRole)表实体类
 *
 * @author yunze
 * @since 2023-02-14 23:48:14
 */
@SuppressWarnings("serial")
public class TbUserRole extends Model<TbUserRole> {

    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 角色 ID
     */
    private Long roleId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

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

