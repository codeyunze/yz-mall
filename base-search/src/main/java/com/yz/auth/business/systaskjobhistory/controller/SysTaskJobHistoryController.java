package com.yz.auth.business.systaskjobhistory.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.yz.common.vo.PageFilter;
import com.yz.common.vo.TableResult;
import com.yz.auth.business.systaskjobhistory.entity.SysTaskJobHistory;
import com.yz.auth.business.systaskjobhistory.service.SysTaskJobHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 系统-任务作业执行历史表 前端控制器
 * </p>
 *
 * @author yunze
 * @since 2022-11-22
 */
@Api(tags = "调度任务作业执行历史信息")
@RestController
@RequestMapping("/jobHistory")
public class SysTaskJobHistoryController {

    @Autowired
    private SysTaskJobHistoryService service;

    @PostMapping(value = "/list")
    @ApiOperation(value = "分页查询调度任务作业所有历史数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "Content-Type", value = "application/json", dataType = "String", required = false),
            @ApiImplicitParam(paramType = "body", name = "filter", value = "分页查询实体", required = false)
    })
    @ApiOperationSupport(author = "yunze")
    public TableResult list(@RequestBody(required = false) PageFilter<SysTaskJobHistory> filter) {
        List<SysTaskJobHistory> list = service.list();
        return TableResult.success(list);
    }
}
