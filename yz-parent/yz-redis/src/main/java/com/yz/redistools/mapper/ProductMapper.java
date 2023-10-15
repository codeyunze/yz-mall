package com.yz.redistools.mapper;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.redistools.common.RedisCacheKey;
import com.yz.redistools.dto.ProductAddDto;
import com.yz.redistools.entity.Product;
import com.yz.redistools.model.ProductModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author : yunze
 * @date : 2023/9/19 12:50
 */
@Slf4j
@Repository
public class ProductMapper {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 创建商品信息
     *
     * @param dto 商品信息及库存
     * @return 商品详情信息及库存
     * @throws JsonProcessingException 数据转换失败
     */
    public ProductModel create(ProductAddDto dto) throws JsonProcessingException {
        long productId = IdUtil.getSnowflakeNextId();
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setId(productId);

        // TODO: 2023/10/15 0015 此处需要加锁
        redisTemplate.opsForValue().set(RedisCacheKey.productCacheKey(productId), new ObjectMapper().writeValueAsString(product));
        redisTemplate.opsForValue().set(RedisCacheKey.productStockCacheKey(productId), dto.getStock().toString());

        log.info(new ObjectMapper().writeValueAsString(product));
        System.out.println("创建商品成功");

        ProductModel model = new ProductModel();
        BeanUtils.copyProperties(product, model);
        model.setStock(dto.getStock());
        return model;
    }

    public Product update(Product product) {
        System.out.println("修改商品成功");
        return product;
    }

    /**
     * 查询商品信息
     *
     * @param productId 商品信息ID
     * @return 商品详情信息及库存
     */
    public ProductModel getInfoById(Long productId) throws JsonProcessingException {
        System.out.println("查询商品成功");
        String info = redisTemplate.opsForValue().get(RedisCacheKey.productCacheKey(productId));
        String stock = redisTemplate.opsForValue().get(RedisCacheKey.productStockCacheKey(productId));
        if (null == info) {
            return null;
        }

        ProductModel model = new ObjectMapper().readValue(info, ProductModel.class);
        assert stock != null;
        model.setStock(Integer.valueOf(stock));
        return model;

    }
}
