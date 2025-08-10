package com.yz.mall.pms.extend;

import com.yz.mall.base.Result;
import com.yz.mall.pms.dto.ExtendPmsProductSlimVo;
import com.yz.mall.pms.service.ExtendPmsProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yunze
 * @date 2025/8/10 星期日 9:27
 */
@RestController
@RequestMapping("extend/pms/product")
public class ExtendPmsProductController {

    private final ExtendPmsProductService extendPmsProductService;

    public ExtendPmsProductController(ExtendPmsProductService extendPmsProductService) {
        this.extendPmsProductService = extendPmsProductService;
    }

    /**
     * 根据商品Id查询对应的商品信息
     *
     * @param productIds 商品Id
     * @return 商品信息
     */
    @PostMapping("getProductByProductIds")
    public Result<List<ExtendPmsProductSlimVo>> getProductByProductIds(@RequestBody List<Long> productIds) {
        return Result.success(extendPmsProductService.getProductByProductIds(productIds));
    }

}
