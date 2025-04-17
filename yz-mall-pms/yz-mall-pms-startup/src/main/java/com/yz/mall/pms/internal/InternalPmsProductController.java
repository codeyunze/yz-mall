package com.yz.mall.pms.internal;

import com.yz.mall.pms.dto.InternalPmsProductSlimVo;
import com.yz.mall.pms.service.InternalPmsProductService;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 内部暴露接口: 商品信息
 *
 * @author yunze
 * @date 2024/6/23 16:21
 */
@RestController
@RequestMapping("internal/pms/product")
public class InternalPmsProductController extends ApiController {

    private final InternalPmsProductService service;

    public InternalPmsProductController(InternalPmsProductService service) {
        this.service = service;
    }

    /**
     * 根据商品Id查询对应的商品信息
     *
     * @param productIds 商品Id
     * @return 商品信息
     */
    @PostMapping("getProductByProductIds")
    public Result<List<InternalPmsProductSlimVo>> getProductByProductIds(@RequestBody List<Long> productIds) {
        return Result.success(service.getProductByProductIds(productIds));
    }

}
