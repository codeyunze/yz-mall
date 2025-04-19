package com.yz.mall.pms.feign;

import com.yz.mall.pms.dto.InternalPmsProductSlimVo;
import com.yz.mall.web.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 内部暴露接口: 商品信息
 *
 * @author yunze
 * @date 2024/6/23 16:21
 */
@Repository
@FeignClient(name = "yz-mall-pms", contextId = "internalPmsProduct", path = "internal/pms/product")
public interface InternalPmsProductFeign {

    /**
     * 根据商品Id查询对应的商品信息
     *
     * @param productIds 商品Id
     * @return 商品信息
     */
    @PostMapping("getProductByProductIds")
    public Result<List<InternalPmsProductSlimVo>> getProductByProductIds(@RequestBody List<Long> productIds);

}
