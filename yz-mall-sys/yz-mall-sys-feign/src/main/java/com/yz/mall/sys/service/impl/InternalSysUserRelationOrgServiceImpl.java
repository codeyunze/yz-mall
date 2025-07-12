package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.feign.InternalSysUserRelationOrgFeign;
import com.yz.mall.sys.service.InternalSysUserRelationOrgService;
import com.yz.mall.sys.vo.InternalSysUserRelationOrgVo;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.enums.CodeEnum;
import com.yz.mall.web.exception.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内部暴露service实现类: 用户关联组织信息
 *
 * @author yunze
 * @since 2025/5/18 23:02
 */
@Service
public class InternalSysUserRelationOrgServiceImpl implements InternalSysUserRelationOrgService {

    private final InternalSysUserRelationOrgFeign internalSysUserRelationOrgFeign;

    public InternalSysUserRelationOrgServiceImpl(InternalSysUserRelationOrgFeign internalSysUserRelationOrgFeign) {
        this.internalSysUserRelationOrgFeign = internalSysUserRelationOrgFeign;
    }

    @Override
    public List<InternalSysUserRelationOrgVo> getOrgByUserId(Long userId) {
        Result<List<InternalSysUserRelationOrgVo>> result = internalSysUserRelationOrgFeign.getOrgByUserId(userId);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }
}
