package com.yz.mall.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.oms.dto.OmsOrderQueryDto;
import com.yz.mall.oms.entity.OmsOrder;
import com.yz.mall.oms.vo.OmsOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单信息表(OmsOrder)表数据库访问层
 *
 * @author yunze
 * @since 2025-01-30 19:12:59
 */
@Mapper
public interface OmsOrderMapper extends BaseMapper<OmsOrder> {

    /**
     * 分页查询
     *
     * @param page   分页
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<OmsOrderVo> selectPageByFilter(Page<Object> page, @Param("filter") OmsOrderQueryDto filter);
}

