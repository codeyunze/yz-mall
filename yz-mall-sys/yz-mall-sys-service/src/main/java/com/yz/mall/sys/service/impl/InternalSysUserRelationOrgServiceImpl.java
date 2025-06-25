package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.service.InternalSysUserRelationOrgService;
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
public class InternalSysUserRelationOrgServiceImpl implements InternalSysUserRelationOrgService {

    private final InternalSysUserRelationOrgService internalSysUserRelationOrgService;

    public InternalSysUserRelationOrgServiceImpl(InternalSysUserRelationOrgService internalSysUserRelationOrgService) {
        this.internalSysUserRelationOrgService = internalSysUserRelationOrgService;
    }

    @Override
    public List<InternalSysUserRelationOrgVo> getOrgByUserId(Long userId) {
        return internalSysUserRelationOrgService.getOrgByUserId(userId);
    }
}
