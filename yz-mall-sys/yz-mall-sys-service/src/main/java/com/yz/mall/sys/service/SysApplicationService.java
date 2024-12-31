package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysApplicationAddDto;
import com.yz.mall.sys.dto.SysApplicationQueryDto;
import com.yz.mall.sys.dto.SysApplicationUpdateDto;
import com.yz.mall.sys.entity.SysApplication;
import com.yz.mall.sys.vo.SysApplicationVo;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;

/**
 * 应用配置(SysApplication)表服务接口
 *
 * @author yunze
 * @since 2024-08-11 20:10:14
 */
public interface SysApplicationService extends IService<SysApplication> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    String save(SysApplicationAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysApplicationUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysApplication> page(PageFilter<SysApplicationQueryDto> filter);

    /**
     * 根据客户端id获取应用客户端信息
     * @param clientId 应用客户端Id
     * @return 应用客户端详细信息
     */
    public SysApplicationVo getByClientId(String clientId);

}

