package com.yz.mall.sys.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.mapper.SysUserMapper;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.SysUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yunze
 * @since 2025/7/10 10:51
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public Page<SysUserVo> page(long current, long size, SysUserQueryDto filter) {
        log.info("用户信息实现类");
        return baseMapper.selectPage(new Page<>(current, size), filter);
    }
}
