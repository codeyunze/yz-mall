package com.yz.mall.pms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.pms.dto.PmsStockInDetailAddDto;
import com.yz.mall.pms.dto.PmsStockInDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockInDetailUpdateDto;
import com.yz.mall.pms.entity.PmsStockInDetail;
import com.yz.mall.pms.vo.PmsStockInDetailVo;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表服务接口
 *
 * @author yunze
 * @since 2024-12-25 19:53:27
 */
public interface PmsStockInDetailService extends IService<PmsStockInDetail> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long in(PmsStockInDetailAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid PmsStockInDetailUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<PmsStockInDetailVo> page(PageFilter<PmsStockInDetailQueryDto> filter);

}

