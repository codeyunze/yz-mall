package com.yz.mall.serial.service.impl;

import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.serial.dto.ExtendUnqidDto;
import com.yz.mall.serial.feign.ExtendSerialFeign;
import com.yz.mall.serial.service.ExtendUnqidService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yunze
 * @date 2024/6/24 星期一 23:35
 */
@Service
public class ExtendSerialServiceImpl implements ExtendUnqidService {

    private final ExtendSerialFeign feign;

    public ExtendSerialServiceImpl(ExtendSerialFeign feign) {
        this.feign = feign;
    }

    @Override
    public String generateNumber(String prefix, Integer numberLength) {
        Result<String> result = feign.generateNumber(new ExtendUnqidDto(prefix, numberLength));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMsg());
        }
        return result.getData();
    }
}
