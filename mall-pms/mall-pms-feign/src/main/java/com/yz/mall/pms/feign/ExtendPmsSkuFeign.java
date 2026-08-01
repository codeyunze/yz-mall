package com.yz.mall.pms.feign;

import com.yz.mall.base.Result;
import com.yz.mall.pms.dto.ExtendPmsSkuSlimVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 内部暴露接口: 商品SKU
 */
@Repository
@FeignClient(name = "mall-pms", contextId = "extendPmsSku", path = "extend/pms/sku")
public interface ExtendPmsSkuFeign {

    /**
     * 根据 SKU Id 批量查询
     *
     * @param skuIds SKU Id 列表
     * @return SKU 信息
     */
    @PostMapping("getSkuByIds")
    Result<List<ExtendPmsSkuSlimVo>> getSkuByIds(@RequestBody List<Long> skuIds);
}
