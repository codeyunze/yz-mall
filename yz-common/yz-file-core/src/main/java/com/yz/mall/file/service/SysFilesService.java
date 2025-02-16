package com.yz.mall.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.file.dto.SysFilesAddDto;
import com.yz.mall.file.dto.SysFilesQueryDto;
import com.yz.mall.file.dto.SysFilesUpdateDto;
import com.yz.mall.file.entity.SysFiles;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;

/**
 * 系统-文件表(SysFiles)表服务接口
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
public interface SysFilesService extends IService<SysFiles> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysFilesAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysFilesUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysFiles> page(PageFilter<SysFilesQueryDto> filter);

}

