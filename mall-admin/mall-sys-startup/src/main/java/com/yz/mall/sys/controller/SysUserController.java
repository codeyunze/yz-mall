package com.yz.mall.sys.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.ResultTable;
import com.yz.mall.sys.dto.SysUserQueryDto;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.SysUserVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基础-用户(BaseUser)表控制层
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

    @GetMapping("page")
    public ResultTable<SysUserVo> page() {
        Page<SysUserVo> result = userService.page(1L, 10L, new SysUserQueryDto());
        return new ResultTable<>(result.getRecords(), result.getTotal());
    }

}

