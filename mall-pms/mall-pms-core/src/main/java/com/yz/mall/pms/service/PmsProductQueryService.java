package com.yz.mall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.pms.entity.PmsProduct;
import com.yz.mall.pms.vo.PmsProductSlimVo;

import java.util.List;

/**
 * 商品表(PmsProduct)表服务接口
 *
 * @author yunze
 * @since 2024-06-16 16:06:43
 */
public interface PmsProductQueryService extends IService<PmsProduct> {

    /**
     * 根据商品 Id查询对应的商品信息
     *
     * @param productIds 商品 Id
     * @return 商品信息
     */
    List<PmsProductSlimVo> getProductByProductIds(List<Long> productIds);

}

