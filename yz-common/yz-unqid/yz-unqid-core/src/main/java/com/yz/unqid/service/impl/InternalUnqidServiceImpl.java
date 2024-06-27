package com.yz.unqid.service.impl;

import com.yz.unqid.service.InternalUnqidService;
import com.yz.unqid.service.SysUnqidService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yunze
 * @date 2024/6/23 星期日 22:59
 */
@Service
public class InternalUnqidServiceImpl implements InternalUnqidService {

    private final SysUnqidService service;

    public InternalUnqidServiceImpl(SysUnqidService service) {
        this.service = service;
    }


    @Override
    public String generateSerialNumber(String prefix, Integer numberLength) {
        return this.service.generateSerialNumber(prefix, numberLength);
    }

    @Override
    public List<String> generateSerialNumbers(String prefix, Integer numberLength, Integer quantity) {
        return this.service.generateSerialNumbers(prefix, numberLength, quantity);
    }
}
