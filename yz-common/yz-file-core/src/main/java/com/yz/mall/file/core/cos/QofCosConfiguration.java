package com.yz.mall.file.core.cos;

import cn.hutool.core.text.CharPool;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import com.yz.mall.file.QofConstant;
import com.yz.mall.file.enums.QofStorageModeEnum;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * 腾讯云COS对象存储客户端操作配置
 *
 * @author yunze
 * @date 2025/2/16 18:26
 */
@ConditionalOnClass(COSClient.class)    // 当项目中存在COSClient.class类时才会使当前配置类生效
@SpringBootConfiguration
@EnableConfigurationProperties({QofCosProperties.class})
// 只有当qof.cos.enable=true时，QofCosConfiguration配置类才会被加载
@ConditionalOnProperty(
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.COS,
        name = QofConstant.ENABLE,
        havingValue = QofConstant.ENABLE_VALUE)
public class QofCosConfiguration {

    @Resource
    private QofCosProperties cosProperties;

    @Bean
    public COSClient cosClient() {
        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(cosProperties.getSecretId(), cosProperties.getSecretKey());
        // 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(cosProperties.getRegion()));
        // 生成cos客户端
        return new COSClient(cred, clientConfig);
    }
}
