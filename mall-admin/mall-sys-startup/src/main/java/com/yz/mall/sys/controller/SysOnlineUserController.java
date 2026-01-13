package com.yz.mall.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.sys.dto.SysOnlineUserQueryDto;
import com.yz.mall.sys.service.SysOnlineUserService;
import com.yz.mall.sys.vo.SysOnlineUserVo;
import com.yz.mall.web.annotation.RepeatSubmit;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统-在线用户管理
 *
 * @author yunze
 * @since 2025-12-11
 */
@RestController
@RequestMapping("sys/onlineUser")
public class SysOnlineUserController {

    private final SysOnlineUserService onlineUserService;

    public SysOnlineUserController(SysOnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService;
    }

    /**
     * 获取在线用户列表
     */
    // @SaCheckPermission("api:system:onlineUser:list")
    @PostMapping("list")
    public Result<ResultTable<SysOnlineUserVo>> list(@RequestBody PageFilter<SysOnlineUserQueryDto> queryDto) {
        List<SysOnlineUserVo> result = onlineUserService.list(queryDto);
        return new Result<>(CodeEnum.SUCCESS.get(), new ResultTable<>(result, (long) result.size()), "查询成功");
    }

    /**
     * 踢下线指定用户
     *
     * @param token 用户token
     */
    // @RepeatSubmit
    // @SaCheckPermission("api:system:onlineUser:kickout")
    // @PostMapping("kickout")
    public Result<Boolean> kickout(@RequestParam String token) {
        return Result.success(this.onlineUserService.kickout(token));
    }

    /**
     * 踢下线指定用户ID的所有会话
     *
     * @param userId 用户ID
     */
    @RepeatSubmit
    // @SaCheckPermission("api:system:onlineUser:kickout")
    @PostMapping("kickoutByUserId/{userId}")
    public Result<Boolean> kickoutByUserId(@PathVariable Long userId) {
        return Result.success(this.onlineUserService.kickoutByUserId(userId));
    }
}

