package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysAccountAuAddDto;
import com.yz.mall.sys.dto.SysAccountAuChooseQueryDto;
import com.yz.mall.sys.dto.SysAccountAuQueryDto;
import com.yz.mall.sys.dto.SysAccountAuUpdateDto;
import com.yz.mall.sys.entity.SysAccountAu;
import com.yz.mall.sys.vo.SysAccountAuChooseVo;
import com.yz.mall.sys.vo.SysAccountAuVo;
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
    Page<SysAccountAuVo> getPageByFilter(PageFilter<SysAccountAuQueryDto> filter);

    /**
     * 分页查询汇总信息
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysAccountAuVo> getPageSummaryByFilter(PageFilter<SysAccountAuQueryDto> filter);

    /**
     * 分页查询还有剩余的买入单
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysAccountAuChooseVo> getChooseByFilter(PageFilter<SysAccountAuChooseQueryDto> filter);

    /**
     * 删除交易记录
     * @param id 交易记录Id
     * @return 是否操作成功
     */
    boolean removeById(Long id);

}

