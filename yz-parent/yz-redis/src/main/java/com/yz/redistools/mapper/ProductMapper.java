package com.yz.redistools.mapper;

import com.yz.redistools.entity.Product;
import org.springframework.stereotype.Repository;

/**
 * @author : yunze
 * @date : 2023/9/19 12:50
 */
@Repository
public class ProductMapper {

    public Product create(Product product) {
        System.out.println("创建商品成功");
        return product;
    }

    public Product update(Product product) {
        System.out.println("修改商品成功");
        return product;
    }

    public Product get(Long productId) {
        System.out.println("查询商品成功");
        Product product = new Product();
        product.setId(productId);
        product.setName("test");
        return product;
    }
}
