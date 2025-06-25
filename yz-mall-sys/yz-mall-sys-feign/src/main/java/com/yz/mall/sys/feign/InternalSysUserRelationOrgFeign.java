package com.yz.mall.sys.feign;

import com.yz.mall.sys.vo.InternalSysUserRelationOrgVo;
import com.yz.mall.web.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 内部暴露接口: 用户关联组织信息
 *
 * @author yunze
 * @since 2025/5/18 23:00
 */
@FeignClient(name = "yz-mall-sys", contextId = "internalSysUserOrg", path = "internal/sys/user/org")
public interface InternalSysUserRelationOrgFeign {

    /**
     * 获取指定用户加入的组织信息
     *
     * @param userId 用户信息
     * @return 用户加入的组织信息
     */
    @GetMapping("getOrgByUserId/{userId}")
    public Result<List<InternalSysUserRelationOrgVo>> getOrgByUserId(@PathVariable Long userId);

}
