package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.feign.ExtendSysUserRelationOrgFeign;
import com.yz.mall.sys.service.ExtendSysUserRelationOrgService;
import com.yz.mall.sys.vo.ExtendSysUserRelationOrgVo;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内部暴露service实现类: 用户关联组织信息
 *
 * @author yunze
 * @since 2025/5/18 23:02
 */
@Service
public class ExtendSysUserRelationOrgServiceImpl implements ExtendSysUserRelationOrgService {

    private final ExtendSysUserRelationOrgFeign internalSysUserRelationOrgFeign;

    public ExtendSysUserRelationOrgServiceImpl(ExtendSysUserRelationOrgFeign internalSysUserRelationOrgFeign) {
        this.internalSysUserRelationOrgFeign = internalSysUserRelationOrgFeign;
    }

    @Override
    public List<ExtendSysUserRelationOrgVo> getOrgByUserId(Long userId) {
        Result<List<ExtendSysUserRelationOrgVo>> result = internalSysUserRelationOrgFeign.getOrgByUserId(userId);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }
}
