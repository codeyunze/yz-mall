package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysOrgAddDto;
import com.yz.mall.sys.dto.SysOrgQueryDto;
import com.yz.mall.sys.dto.SysOrgUpdateDto;
import com.yz.mall.sys.entity.SysOrg;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统-组织表(SysOrg)表服务接口
 *
 * @author yunze
 * @since 2024-11-17 20:19:07
 */
public interface SysOrgService extends IService<SysOrg> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysOrgAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysOrgUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysOrg> page(PageFilter<SysOrgQueryDto> filter);

    /**
     * 列表查询
     *
     * @param filter 过滤条件
     * @return 列表数据
     */
    List<SysOrg> list(SysOrgQueryDto filter);

}

