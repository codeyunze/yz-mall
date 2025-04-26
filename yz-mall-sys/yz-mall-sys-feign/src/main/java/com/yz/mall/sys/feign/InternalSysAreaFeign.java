package com.yz.mall.sys.feign;

import com.yz.mall.sys.vo.InternalSysAreaVo;
import com.yz.mall.web.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 内部暴露接口: 系统管理-地区接口
 * @author yunze
 * @since 2025/4/21 19:42
 */
@FeignClient(name = "yz-mall-sys", contextId = "internalArea", path = "internal/area")
public interface InternalSysAreaFeign {

    /**
     * 根据行政地区Id（行政地区编码）获取该行政地区信息
     *
     * @param id 行政地区Id（行政地区编码）
     * @return 行政地区信息
     */
    @GetMapping("get/{id}")
    Result<InternalSysAreaVo> getById(@PathVariable String id);
}
