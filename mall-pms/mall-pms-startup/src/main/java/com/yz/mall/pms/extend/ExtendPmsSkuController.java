package com.yz.mall.pms.extend;

import com.yz.mall.base.Result;
import com.yz.mall.pms.dto.ExtendPmsSkuSlimVo;
import com.yz.mall.pms.service.ExtendPmsSkuService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 内部暴露接口: 商品SKU
 */
@RestController
@RequestMapping("extend/pms/sku")
public class ExtendPmsSkuController {

    private final ExtendPmsSkuService extendPmsSkuService;

    public ExtendPmsSkuController(ExtendPmsSkuService extendPmsSkuService) {
        this.extendPmsSkuService = extendPmsSkuService;
    }

    /**
     * 根据 SKU Id 批量查询
     *
     * @param skuIds SKU Id 列表
     * @return SKU 信息
     */
    @PostMapping("getSkuByIds")
    public Result<List<ExtendPmsSkuSlimVo>> getSkuByIds(@RequestBody List<Long> skuIds) {
        return Result.success(extendPmsSkuService.getSkuByIds(skuIds));
    }
}
