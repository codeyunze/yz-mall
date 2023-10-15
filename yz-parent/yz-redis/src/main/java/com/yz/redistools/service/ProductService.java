package com.yz.redistools.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.redistools.dto.ProductAddDto;
import com.yz.redistools.mapper.ProductMapper;
import com.yz.redistools.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yunze
 * @date 2023/10/15 0015 23:28
 */
@Service
public class ProductService {

    @Autowired
    private ProductMapper mapper;

    public ProductModel getInfoById(Long id) {
        try {
            return mapper.getInfoById(id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ProductModel create(ProductAddDto dto) {
        try {
            return mapper.create(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
