package com.yz.mall.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置属性
 * @author yunze
 * @date 2025/2/16 16:28
 */
@Data
@Configuration
@ConfigurationProperties(
        prefix = QofConstant.QOF
)
public class QofProperties {

    /**
     * 是否开始文件信息数据持久化
     */
    boolean persistentEnable;

}
