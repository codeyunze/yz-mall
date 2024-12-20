package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysUserRelationRoleBindDto;
import com.yz.mall.sys.dto.SysUserRelationRoleQueryDto;
import com.yz.mall.sys.dto.SysUserRelationRoleUpdateDto;
import com.yz.mall.sys.entity.SysUserRelationRole;
import com.yz.mall.sys.service.SysUserRelationRoleService;
import com.yz.tools.ApiController;
import com.yz.tools.PageFilter;
import com.yz.tools.Result;
import com.yz.tools.ResultTable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 系统-关联角色数据表(SysUserRelationRole)表控制层
 *
 * @author yunze
 * @since 2024-11-17 19:55:59
 */
@RestController
@RequestMapping("sys/user/role")
public class SysUserRelationRoleController extends ApiController {

    private final SysUserRelationRoleService service;

    public SysUserRelationRoleController(SysUserRelationRoleService service) {
        this.service = service;
    }

    /**
     * 给用户或组织分配角色
     * @apiNote 用户组织信息与角色信息绑定
     */
    @SaCheckPermission("api:system:userRelationRole:bind")
    @PostMapping("bind")
    public Result<Boolean> bind(@RequestBody @Valid SysUserRelationRoleBindDto dto) {
        return success(this.service.bind(dto));
    }

    /**
     * 获取请求用户所拥有的角色Id
     *
     * @return 请求用户所拥有的角色Id
     */
    @GetMapping("getRoleId")
    public Result<List<Long>> getRole() {
        long userId = StpUtil.getLoginIdAsLong();
        List<Long> roleIdsByRelationId = service.getRoleIdsByRelationId(userId);
        return success(roleIdsByRelationId);
    }
}

