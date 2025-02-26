package com.yz.mall.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 文件服务
 *
 * @author yunze
 * @date 2024/7/9 星期二 23:32
 */
@ComponentScan({"com.yz.mall.file"})
@MapperScan({"com.yz.mall.file.mapper"})
@Configuration
public class YzMallFileCoreConfig {

}
