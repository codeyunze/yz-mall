package com.yz.mall.sys.service.impl;

import com.yz.mall.base.exception.DataNotExistException;
import com.yz.mall.sys.entity.SysArea;
import com.yz.mall.sys.service.ExtendSysAreaService;
import com.yz.mall.sys.service.SysAreaService;
import com.yz.mall.sys.vo.ExtendSysAreaVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 内部暴露service实现类: 系统管理-地区接口
 *
 * @author yunze
 * @since 2025/4/21 19:06
 */
@Service
public class ExtendSysAreaServiceImpl implements ExtendSysAreaService {

    private final SysAreaService service;

    public ExtendSysAreaServiceImpl(SysAreaService service) {
        this.service = service;
    }

    // 缓存空值（防穿透）
    @Cacheable(value = "system:area-detail", key = "#id", unless = "#result == null")
    @Override
    public ExtendSysAreaVo getById(String id) {
        SysArea area = service.getById(id);
        if (area != null) {
            ExtendSysAreaVo vo = new ExtendSysAreaVo();
            BeanUtils.copyProperties(area, vo);
            return vo;
        }

        throw new DataNotExistException("[" + id + "] 行政地区不存在");
    }
}
