package com.yz.openinterface.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @ClassName SwaggerConfig
 * @Description TODO
 * @Author yunze
 * @Date 2023/2/12 17:52
 * @Version 1.0
 */
@Configuration
@EnableSwagger2WebMvc
@EnableKnife4j
public class SwaggerConfig {
    /**
     * 创建RestApi 并包扫描controller
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.yz.openinterface.business"))
                .paths(PathSelectors.any())
                // .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    /**
     * 创建Swagger页面 信息
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().
                title("订单模块").
                contact("yunze").
                version("1.0 version").
                termsOfServiceUrl("api/order/**").
                description("开放接口服务-这是一个 cloud+nacos+gateway+rabbitmq+knife4j 的项目")
                .build();
    }
}
