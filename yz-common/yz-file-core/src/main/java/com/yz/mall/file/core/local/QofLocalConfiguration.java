package com.yz.mall.file.core.local;

import cn.hutool.core.text.CharPool;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import com.yz.mall.file.QofConstant;
import com.yz.mall.file.core.cos.QofCosProperties;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * 本地存储操作配置
 *
 * @author yunze
 * @date 2025/2/16 18:26
 */
@SpringBootConfiguration
@EnableConfigurationProperties({QofLocalProperties.class})
// 只有当qof.cos.enable=true时，QofLocalConfiguration配置类才会被加载
@ConditionalOnProperty(
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.LOCAL,
        name = QofConstant.ENABLE,
        havingValue = QofConstant.ENABLE_VALUE)
public class QofLocalConfiguration {

}
