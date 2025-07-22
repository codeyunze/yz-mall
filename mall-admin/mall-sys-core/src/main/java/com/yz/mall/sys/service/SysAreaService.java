package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.sys.dto.SysAreaAddDto;
import com.yz.mall.sys.dto.SysAreaQueryDto;
import com.yz.mall.sys.dto.SysAreaUpdateDto;
import com.yz.mall.sys.entity.SysArea;
import com.yz.mall.sys.vo.AreaVo;

import jakarta.validation.Valid;
import java.util.List;

/**
 * (SysArea)表服务接口
 *
 * @author yunze
 * @since 2025-03-03 22:38:56
 */
public interface SysAreaService extends IService<SysArea> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    String save(SysAreaAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysAreaUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysArea> page(PageFilter<SysAreaQueryDto> filter);

    /**
     * 获取指定地区下的管辖地区
     *
     * @param parentId 指定地区
     * @return 所管辖的地区
     */
    List<AreaVo> getByParentId(String parentId);

}

