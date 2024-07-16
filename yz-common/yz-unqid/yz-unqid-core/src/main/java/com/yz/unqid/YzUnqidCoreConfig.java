package com.yz.unqid;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yunze
 * @date 2024/6/23 星期日 22:47
 */
@Configuration
@EnableScheduling
@ComponentScan({"com.yz.unqid"})
public class YzUnqidCoreConfig {
}
