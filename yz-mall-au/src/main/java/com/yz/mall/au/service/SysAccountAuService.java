package com.yz.mall.au.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.au.dto.SysAccountAuAddDto;
import com.yz.mall.au.dto.SysAccountAuQueryDto;
import com.yz.mall.au.dto.SysAccountAuUpdateDto;
import com.yz.mall.au.entity.SysAccountAu;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;

/**
 * 个人黄金账户(SysAccountAu)表服务接口
 *
 * @author yunze
 * @since 2025-01-05 10:06:32
 */
public interface SysAccountAuService extends IService<SysAccountAu> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysAccountAuAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysAccountAuUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysAccountAu> page(PageFilter<SysAccountAuQueryDto> filter);

}

