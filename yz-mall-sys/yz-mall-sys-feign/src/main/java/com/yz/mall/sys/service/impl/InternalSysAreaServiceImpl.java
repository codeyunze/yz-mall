package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.feign.InternalSysAreaFeign;
import com.yz.mall.sys.service.InternalSysAreaService;
import com.yz.mall.sys.vo.InternalSysAreaVo;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.enums.CodeEnum;
import com.yz.mall.web.exception.FeignException;
import org.springframework.stereotype.Service;

/**
 * 内部暴露service实现类: 系统管理-地区接口
 *
 * @author yunze
 * @since 2025/4/21 19:36
 */
@Service
public class InternalSysAreaServiceImpl implements InternalSysAreaService {

    private final InternalSysAreaFeign areaFeign;

    public InternalSysAreaServiceImpl(InternalSysAreaFeign areaFeign) {
        this.areaFeign = areaFeign;
    }

    @Override
    public InternalSysAreaVo getById(String id) {
        Result<InternalSysAreaVo> result = areaFeign.getById(id);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

}
