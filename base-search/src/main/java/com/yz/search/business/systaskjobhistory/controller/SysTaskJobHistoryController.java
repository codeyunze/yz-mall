package com.yz.search.business.systaskjobhistory.controller;


import com.yz.common.vo.TableResult;
import com.yz.search.business.systaskjobhistory.entity.SysTaskJobHistory;
import com.yz.search.business.systaskjobhistory.service.SysTaskJobHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RestController
@RequestMapping("/systaskjobhistory")
public class SysTaskJobHistoryController {

    @Autowired
    private SysTaskJobHistoryService service;

    @RequestMapping(value = "/list")
    public TableResult list() {
        List<SysTaskJobHistory> list = service.list();
        return TableResult.success(list.size(), list);
    }
}
