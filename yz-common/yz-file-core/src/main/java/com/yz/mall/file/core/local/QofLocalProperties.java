package com.yz.mall.file.core.local;

import cn.hutool.core.text.CharPool;
import com.yz.mall.file.QofConstant;
import com.yz.mall.file.core.cos.QofCosModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 本地文件存储属性配置信息
 *
 * @author yunze
 * @date 2025/2/16 16:28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(
        // qof.local
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.LOCAL
)
public class QofLocalProperties extends QofLocalModel {

    /**
     * 是否启用腾讯云COS对象存储
     */
    private boolean enable;

    /**
     * 多个文件存储路径配置信息
     * Map<Bean名称, 本地文件存储的属性配置信息>
     */
    Map<String, QofLocalModel> multiple;

}


