package com.yz.mall.serial.service.impl;

import com.yz.mall.serial.service.ExtendUnqidService;
import com.yz.mall.serial.service.SerialService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * @author yunze
 * @date 2024/6/23 星期日 22:59
 */
@Service
public class ExtendSerialServiceImpl implements ExtendUnqidService {

    private final SerialService service;

    public ExtendSerialServiceImpl(@Qualifier("serialPoolServiceImpl") SerialService service) {
        this.service = service;
    }


    @Override
    public String generateNumber(String prefix, Integer numberLength) {
        return this.service.generateSerialNumber(prefix, numberLength);
    }
}
