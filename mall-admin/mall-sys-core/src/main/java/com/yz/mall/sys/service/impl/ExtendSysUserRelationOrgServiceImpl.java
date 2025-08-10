package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.service.ExtendSysUserRelationOrgService;
import com.yz.mall.sys.service.SysUserRelationOrgService;
import com.yz.mall.sys.vo.ExtendSysUserRelationOrgVo;
import com.yz.mall.sys.vo.SysUserRelationOrgVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内部暴露service实现类: 用户关联组织信息
 *
 * @author yunze
 * @since 2025/5/18 22:47
 */
@Service
public class ExtendSysUserRelationOrgServiceImpl implements ExtendSysUserRelationOrgService {

    private final SysUserRelationOrgService sysUserRelationOrgService;

    public ExtendSysUserRelationOrgServiceImpl(SysUserRelationOrgService sysUserRelationOrgService) {
        this.sysUserRelationOrgService = sysUserRelationOrgService;
    }

    @Override
    public List<ExtendSysUserRelationOrgVo> getOrgByUserId(Long userId) {
        List<SysUserRelationOrgVo> list = sysUserRelationOrgService.getOrgByUserId(userId);
        return list.stream().map(t -> {
            ExtendSysUserRelationOrgVo vo = new ExtendSysUserRelationOrgVo();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).toList();
    }
}
