package com.yz.mall.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.sys.dto.SysMsgRetryQueryDto;
import com.yz.mall.sys.entity.SysMsgRetry;
import com.yz.mall.sys.service.SysMsgRetryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统-消息重试管理
 *
 * @author yunze
 * @since 2025-01-20
 */
@RestController
@RequestMapping("sys/msgRetry")
public class SysMsgRetryController extends ApiController {

    private final SysMsgRetryService sysMsgRetryService;

    public SysMsgRetryController(SysMsgRetryService sysMsgRetryService) {
        this.sysMsgRetryService = sysMsgRetryService;
    }

    /**
     * 分页查询
     */
    @PostMapping("page")
    public Result<ResultTable<SysMsgRetry>> page(@RequestBody @Valid PageFilter<SysMsgRetryQueryDto> filter) {
        IPage<SysMsgRetry> page = sysMsgRetryService.page(filter.getCurrent(), filter.getSize(), filter.getFilter());
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("get/{id}")
    public Result<SysMsgRetry> getById(@PathVariable Long id) {
        return success(sysMsgRetryService.getById(id));
    }
}

