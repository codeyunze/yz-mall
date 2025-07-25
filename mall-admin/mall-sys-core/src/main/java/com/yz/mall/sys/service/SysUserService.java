package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.vo.SysUserVo;

/**
 * 基础-用户(BaseUser)表服务接口
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysUserVo> page(long current, long size, SysUserQueryDto filter);

}

