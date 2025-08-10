package com.yz.mall.sys.extend;

import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.sys.service.ExtendSysAreaService;
import com.yz.mall.sys.vo.ExtendSysAreaVo;
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
@RequestMapping("extend/area")
public class ExtendSysAreaController extends ApiController {

    private final ExtendSysAreaService areaService;

    public ExtendSysAreaController(ExtendSysAreaService areaService) {
        this.areaService = areaService;
    }

    /**
     * 根据行政地区Id（行政地区编码）获取该行政地区信息
     *
     * @param id 行政地区Id（行政地区编码）
     * @return 行政地区信息
     */
    @GetMapping("get/{id}")
    public Result<ExtendSysAreaVo> getById(@PathVariable String id) {
        return success(areaService.getById(id));
    }
}
