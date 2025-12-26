package com.yz.mall.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.sys.dto.SysLoginLogQueryDto;
import com.yz.mall.sys.service.SysLoginLogService;
import com.yz.mall.sys.vo.SysLoginLogVo;
import com.yz.mall.web.annotation.RepeatSubmit;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 系统-登录日志管理
 *
 * @author yunze
 * @since 2025-12-11
 */
@RestController
@RequestMapping("sys/loginLog")
public class SysLoginLogController {

    private final SysLoginLogService loginLogService;

    public SysLoginLogController(SysLoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:system:loginLog:page")
    @PostMapping("page")
    public Result<ResultTable<SysLoginLogVo>> page(@RequestBody @Valid PageFilter<SysLoginLogQueryDto> filter) {
        Page<SysLoginLogVo> result = loginLogService.page(filter);
        return new Result<>(CodeEnum.SUCCESS.get(), new ResultTable<>(result.getRecords(), result.getTotal()), "查询成功");
    }

    /**
     * 清空日志
     */
    @RepeatSubmit
    @SaCheckPermission("api:system:loginLog:clear")
    @PostMapping("clear")
    public Result<Boolean> clear() {
        return Result.success(this.loginLogService.clearLogs());
    }
}

