package com.yz.mall.sys.internal;

import com.yz.mall.sys.service.InternalSysUserRelationOrgService;
import com.yz.mall.sys.vo.InternalSysUserRelationOrgVo;
import com.yz.mall.web.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 内部暴露接口: 用户关联组织信息
 *
 * @author yunze
 * @since 2025/5/18 22:57
 */
@RestController
@RequestMapping("internal/sys/user/org")
public class InternalSysUserRelationOrgController {

    private final InternalSysUserRelationOrgService internalSysUserRelationOrgService;

    public InternalSysUserRelationOrgController(InternalSysUserRelationOrgService internalSysUserRelationOrgService) {
        this.internalSysUserRelationOrgService = internalSysUserRelationOrgService;
    }

    /**
     * 获取指定用户加入的组织信息
     *
     * @param userId 用户信息
     * @return 用户加入的组织信息
     */
    @GetMapping("getOrgByUserId/{userId}")
    public Result<List<InternalSysUserRelationOrgVo>> getOrgByUserId(@PathVariable Long userId) {
        return Result.success(internalSysUserRelationOrgService.getOrgByUserId(userId));
    }
}
