package com.yz.mall.sys.service.impl;

import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.FeignException;
import com.yz.mall.sys.feign.ExtendSysAreaFeign;
import com.yz.mall.sys.service.ExtendSysAreaService;
import com.yz.mall.sys.vo.ExtendSysAreaVo;
import org.springframework.stereotype.Service;

/**
 * 内部暴露service实现类: 系统管理-地区接口
 *
 * @author yunze
 * @since 2025/4/21 19:36
 */
@Service
public class ExtendSysAreaServiceImpl implements ExtendSysAreaService {

    private final ExtendSysAreaFeign areaFeign;

    public ExtendSysAreaServiceImpl(ExtendSysAreaFeign areaFeign) {
        this.areaFeign = areaFeign;
    }

    @Override
    public ExtendSysAreaVo getById(String id) {
        Result<ExtendSysAreaVo> result = areaFeign.getById(id);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

}
