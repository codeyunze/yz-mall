package com.yz.mall.sys.internal;

import com.yz.mall.sys.service.InternalSysAreaService;
import com.yz.mall.sys.vo.InternalSysAreaVo;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部暴露接口: 系统管理-行政地区
 * @author yunze
 * @since 2025/4/21 19:36
 */
@RestController
@RequestMapping("internal/area")
public class InternalSysAreaController extends ApiController {

    private final InternalSysAreaService areaService;

    public InternalSysAreaController(InternalSysAreaService areaService) {
        this.areaService = areaService;
    }

    /**
     * 根据行政地区Id（行政地区编码）获取该行政地区信息
     *
     * @param id 行政地区Id（行政地区编码）
     * @return 行政地区信息
     */
    @GetMapping("get/{id}")
    public Result<InternalSysAreaVo> getById(@PathVariable String id) {
        return success(areaService.getById(id));
    }
}
