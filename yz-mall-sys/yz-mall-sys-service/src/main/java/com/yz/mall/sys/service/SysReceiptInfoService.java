package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysReceiptInfoAddDto;
import com.yz.mall.sys.dto.SysReceiptInfoQueryDto;
import com.yz.mall.sys.dto.SysReceiptInfoUpdateDto;
import com.yz.mall.sys.entity.SysReceiptInfo;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;

/**
 * 系统-用户收货信息(SysReceiptInfo)表服务接口
 *
 * @author yunze
 * @since 2025-03-03 15:40:27
 */
public interface SysReceiptInfoService extends IService<SysReceiptInfo> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysReceiptInfoAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysReceiptInfoUpdateDto dto);

    /**
     * 删除数据
     *
     * @param id     数据主键Id
     * @param userId 操作人用户Id
     * @return 是否操作成功
     */
    boolean removeById(Long id, Long userId);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysReceiptInfo> page(PageFilter<SysReceiptInfoQueryDto> filter);

}

