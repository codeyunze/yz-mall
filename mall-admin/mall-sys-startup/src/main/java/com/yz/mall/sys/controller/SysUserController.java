package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.dto.SysUserResetPasswordDto;
import com.yz.mall.sys.dto.SysUserUpdateDto;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.SysTreeMenuVo;
import com.yz.mall.sys.vo.SysUserVo;
import com.yz.mall.web.annotation.RepeatSubmit;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;

/**
 * 系统-用户管理
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@RestController
@RequestMapping("sys/user")
public class SysUserController {

    private final SysUserService userService;

    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<ResultTable<SysUserVo>> page(@RequestBody @Valid PageFilter<SysUserQueryDto> filter) {
        Page<SysUserVo> result = userService.page(filter.getCurrent(), filter.getSize(), filter.getFilter());
        return new Result<>(CodeEnum.SUCCESS.get(), new ResultTable<>(result.getRecords(), result.getTotal()), "查询成功");
    }

    /**
     * 获取请求用户可访问的菜单信息
     */
    @GetMapping("getUserMenus")
    public Result<List<SysTreeMenuVo>> getUserMenus() {
        List<SysTreeMenuVo> menus = userService.getUserMenus(StpUtil.getLoginIdAsLong());
        return new Result<>(CodeEnum.SUCCESS.get(), menus, "查询成功");
    }

    /**
     * 获取指定用户所拥有的角色
     *
     * @param userId 用户Id
     * @return 用户所拥有的角色
     */
    @SaCheckPermission("api:system:user:getUserRoles")
    @GetMapping("getUserRoles/{userId}")
    public Result<List<String>> getUserRoles(@PathVariable Long userId) {
        List<Long> roles = userService.getUserRoles(userId);
        if (CollectionUtils.isEmpty(roles)) {
            return Result.success(Collections.emptyList());
        }
        return Result.success(roles.stream().map(String::valueOf).toList());
    }

    /**
     * 重置用户密码
     *
     * @param dto 用户Id和用户密码
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:user:resetPassword")
    @PostMapping("resetPassword")
    public Result<Boolean> resetPassword(@RequestBody @Valid SysUserResetPasswordDto dto) {
        return Result.success(this.userService.resetPassword(dto));
    }

    /**
     * 更新
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:user:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysUserUpdateDto dto) {
        return Result.success(this.userService.update(dto));
    }

    /**
     * 切换角色状态
     *
     * @param id 角色Id
     * @return 是否切换成功
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:user:switch")
    @PostMapping("switch/{id}")
    public Result<Boolean> switchRole(@PathVariable Long id) {
        boolean updated = userService.updateUserStatusById(id);
        return updated ? Result.success(true) : new Result<>(CodeEnum.BUSINESS_ERROR.get(), false, "角色状态切换失败");
    }
}

