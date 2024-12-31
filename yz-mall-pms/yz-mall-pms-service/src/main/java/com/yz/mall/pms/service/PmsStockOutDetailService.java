package com.yz.mall.pms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.pms.dto.InternalPmsStockDto;
import com.yz.mall.pms.dto.PmsStockOutDetailAddDto;
import com.yz.mall.pms.dto.PmsStockOutDetailQueryDto;
import com.yz.mall.pms.dto.PmsStockOutDetailUpdateDto;
import com.yz.mall.pms.entity.PmsStockOutDetail;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;
import java.util.List;

/**
 * 产品管理-商品出库日志表(PmsStockOutDetail)表服务接口
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
public interface PmsStockOutDetailService extends IService<PmsStockOutDetail> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long out(PmsStockOutDetailAddDto dto);

    /**
     * 出库明细
     *
     * @param outDetails 出库信息
     * @return 是否操作成功
     */
    boolean outBatch(List<InternalPmsStockDto> outDetails);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid PmsStockOutDetailUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<PmsStockOutDetail> page(PageFilter<PmsStockOutDetailQueryDto> filter);

}

