package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.entity.SysArea;
import com.yz.mall.sys.service.InternalSysAreaService;
import com.yz.mall.sys.service.SysAreaService;
import com.yz.mall.sys.vo.InternalSysAreaVo;
import com.yz.mall.web.exception.BusinessException;
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
public class InternalSysAreaServiceImpl implements InternalSysAreaService {

    private final SysAreaService service;

    public InternalSysAreaServiceImpl(SysAreaService service) {
        this.service = service;
    }

    @Cacheable(value = "system:area-detail", key = "#id")
    @Override
    public InternalSysAreaVo getById(String id) {
        SysArea area = service.getById(id);
        if (area != null) {
            InternalSysAreaVo vo = new InternalSysAreaVo();
            BeanUtils.copyProperties(area, vo);
            return vo;
        }

        throw new BusinessException("[" + id + "] 行政地区不存在");
    }
}
