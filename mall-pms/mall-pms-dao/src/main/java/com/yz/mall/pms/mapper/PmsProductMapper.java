package com.yz.mall.pms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.pms.dto.PmsProductSlimQueryDto;
import com.yz.mall.pms.entity.PmsProduct;
import com.yz.mall.pms.vo.PmsProductDisplayInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品表(PmsProduct)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
@Mapper
public interface PmsProductMapper extends BaseMapper<PmsProduct> {

    /**
     * 查询商品展示信息
     *
     * @param filter 数据过滤条件
     * @return 商品展示信息
     */
    List<PmsProductDisplayInfoVo> selectProductDisplayInfoList(@Param("filter") PmsProductSlimQueryDto filter);
}

