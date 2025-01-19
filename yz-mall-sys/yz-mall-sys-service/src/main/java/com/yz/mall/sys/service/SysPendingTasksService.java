package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysPendingTasksAddDto;
import com.yz.mall.sys.dto.SysPendingTasksQueryDto;
import com.yz.mall.sys.dto.SysPendingTasksUpdateDto;
import com.yz.mall.sys.entity.SysPendingTasks;
import com.yz.mall.web.common.IdDto;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;

/**
 * 系统-待办任务(SysPendingTasks)表服务接口
 *
 * @author yunze
 * @since 2025-01-15 16:58:30
 */
public interface SysPendingTasksService extends IService<SysPendingTasks> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysPendingTasksAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysPendingTasksUpdateDto dto);

    /**
     * 结束任务
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean end(@Valid IdDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysPendingTasks> page(PageFilter<SysPendingTasksQueryDto> filter);

}

