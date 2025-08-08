package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.service.ExtendSysUserRelationOrgService;
import com.yz.mall.sys.vo.InternalSysUserRelationOrgVo;
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

    private final ExtendSysUserRelationOrgService extendSysUserRelationOrgService;

    public ExtendSysUserRelationOrgServiceImpl(ExtendSysUserRelationOrgService extendSysUserRelationOrgService) {
        this.extendSysUserRelationOrgService = extendSysUserRelationOrgService;
    }

    @Override
    public List<InternalSysUserRelationOrgVo> getOrgByUserId(Long userId) {
        return extendSysUserRelationOrgService.getOrgByUserId(userId);
    }
}
