package com.yz.mall.sys.extend;

import com.yz.mall.base.Result;
import com.yz.mall.sys.service.ExtendSysUserRelationOrgService;
import com.yz.mall.sys.vo.InternalSysUserRelationOrgVo;
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
@RequestMapping("extend/sys/user/org")
public class ExtendSysUserRelationOrgController {

    private final ExtendSysUserRelationOrgService extendSysUserRelationOrgService;

    public ExtendSysUserRelationOrgController(ExtendSysUserRelationOrgService extendSysUserRelationOrgService) {
        this.extendSysUserRelationOrgService = extendSysUserRelationOrgService;
    }

    /**
     * 获取指定用户加入的组织信息
     *
     * @param userId 用户信息
     * @return 用户加入的组织信息
     */
    @GetMapping("getOrgByUserId/{userId}")
    public Result<List<InternalSysUserRelationOrgVo>> getOrgByUserId(@PathVariable Long userId) {
        return Result.success(extendSysUserRelationOrgService.getOrgByUserId(userId));
    }
}
