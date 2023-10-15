package com.yz.redistools.controller;

import com.yz.redistools.dto.ProductAddDto;
import com.yz.redistools.model.ProductModel;
import com.yz.redistools.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunze
 * @date 2023/7/31 0031 23:20
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/getInfo/{productId}")
    public ProductModel getInfo(@PathVariable Long productId) {
        return productService.getInfoById(productId);
    }

    @RequestMapping("/add")
    public ProductModel add(@RequestBody ProductAddDto dto) {
        return productService.create(dto);
    }
}
