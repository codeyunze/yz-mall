package com.yz.mall.sys.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.SysTreeMenuVo;
import com.yz.mall.sys.vo.SysUserVo;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
}

