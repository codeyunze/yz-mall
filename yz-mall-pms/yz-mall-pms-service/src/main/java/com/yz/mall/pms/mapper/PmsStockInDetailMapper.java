package com.yz.mall.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.pms.entity.PmsStockInDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品管理-商品入库日志表(PmsStockInDetail)表数据库访问层
 *
 * @author yunze
 * @since 2024-12-25 19:53:27
 */
@Mapper
public interface PmsStockInDetailMapper extends BaseMapper<PmsStockInDetail> {

}

