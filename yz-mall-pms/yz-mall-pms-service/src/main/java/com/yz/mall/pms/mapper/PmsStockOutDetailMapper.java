package com.yz.mall.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.pms.entity.PmsStockOutDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品管理-商品出库日志表(PmsStockOutDetail)表数据库访问层
 *
 * @author yunze
 * @since 2024-12-27 12:50:27
 */
@Mapper
public interface PmsStockOutDetailMapper extends BaseMapper<PmsStockOutDetail> {

}

