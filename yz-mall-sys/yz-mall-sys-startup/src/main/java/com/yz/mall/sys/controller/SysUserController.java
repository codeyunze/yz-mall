package com.yz.mall.sys.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysUserAddDto;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.dto.SysUserResetPasswordDto;
import com.yz.mall.sys.dto.SysUserUpdateDto;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.SysTreeMenuVo;
import com.yz.mall.sys.vo.SysUserVo;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.common.ResultTable;
import com.yz.mall.web.enums.CodeEnum;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础-用户(BaseUser)表控制层
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@RestController
@RequestMapping("sys/user")
public class SysUserController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private SysUserService service;

    /**
     * 新增
     */
    @SaCheckPermission("api:system:user:add")
    @PostMapping("add")
    public Result<String> insert(@RequestBody @Valid SysUserAddDto dto) {
        return success(this.service.save(dto));
    }

    /**
     * 更新
     */
    @SaCheckPermission("api:system:user:edit")
    @PostMapping("update")
    public Result<Boolean> update(@RequestBody @Valid SysUserUpdateDto dto) {
        return success(this.service.update(dto));
    }

    /**
     * 切换角色状态
     *
     * @param id 角色Id
     * @return 是否切换成功
     */
    @SaCheckPermission("api:system:user:switch")
    @PostMapping("switch/{id}")
    public Result<Boolean> switchRole(@PathVariable Long id) {
        boolean updated = service.updateUserStatusById(id);
        return updated ? success(true) : error(CodeEnum.BUSINESS_ERROR);
    }

    /**
     * 删除
     *
     * @param id 删除数据主键ID
     */
    @SaCheckPermission("api:system:user:delete")
    @DeleteMapping("delete/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        return success(this.service.removeById(id));
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:system:user:list")
    @PostMapping("page")
    public Result<ResultTable<SysUserVo>> page(@RequestBody @Valid PageFilter<SysUserQueryDto> filter) {
        Page<SysUserVo> page = this.service.page(filter);
        return new Result<>(CodeEnum.SUCCESS.get(), new ResultTable<>(page.getRecords(), page.getTotal()), "查询成功");
    }

    /**
     * 详情查询
     */
    @SaCheckPermission("api:system:user:list")
    @GetMapping("get/{id}")
    public Result<SysUser> page(@PathVariable String id) {
        return success(this.service.getById(id));
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
        List<Long> roles = service.getUserRoles(userId);
        if (CollectionUtils.isEmpty(roles)) {
            return success(Collections.emptyList());
        }
        return success(roles.stream().map(String::valueOf).collect(Collectors.toList()));
    }

    /**
     * 获取请求用户可访问的菜单信息
     */
    @GetMapping("getUserMenus")
    public Result<List<SysTreeMenuVo>> getUserMenus() {
        return success(service.getUserMenus(StpUtil.getLoginIdAsLong()));
    }

    /**
     * 重置用户密码
     * @param dto 用户Id和用户密码
     */
    @SaCheckPermission("api:system:user:resetPassword")
    @PostMapping("resetPassword")
    public Result<Boolean> resetPassword(@RequestBody @Valid SysUserResetPasswordDto dto) {
        return success(this.service.resetPassword(dto));
    }
}

