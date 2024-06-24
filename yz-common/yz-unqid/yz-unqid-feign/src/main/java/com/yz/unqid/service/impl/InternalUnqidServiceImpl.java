package com.yz.unqid.service.impl;

import com.yz.advice.exception.BusinessException;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import com.yz.unqid.dto.InternalUnqidDto;
import com.yz.unqid.feign.InternalUnqidFeign;
import com.yz.unqid.service.InternalUnqidService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yunze
 * @date 2024/6/24 星期一 23:35
 */
@Service
public class InternalUnqidServiceImpl implements InternalUnqidService {

    private final InternalUnqidFeign feign;

    public InternalUnqidServiceImpl(InternalUnqidFeign feign) {
        this.feign = feign;
    }

    @Override
    public String generateSerialNumber(String prefix, Integer numberLength) {
        Result<String> result = feign.generateSerialNumber(new InternalUnqidDto(prefix, numberLength));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMessage());
        }
        return result.getData();
    }

    @Override
    public List<String> generateSerialNumbers(String prefix, Integer numberLength, Integer quantity) {
        Result<List<String>> result = feign.generateSerialNumbers(new InternalUnqidDto(prefix, numberLength, quantity));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new BusinessException(result.getMessage());
        }
        return result.getData();
    }
}
