package com.yz.mall.file.core.cos;

import cn.hutool.core.text.CharPool;
import com.yz.mall.file.QofConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 腾讯云-COS对象存储属性配置信息
 *
 * @author yunze
 * @date 2025/2/16 16:28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(
        // qof.cos
        prefix = QofConstant.QOF + CharPool.DOT + QofConstant.StorageMode.COS
)
public class QofCosProperties extends QofCosModel {

    /**
     * 是否启用腾讯云COS对象存储
     */
    private boolean enable;

    /**
     * 多个COS配置信息
     * Map<Bean名称, COS配置信息>
     */
    Map<String, QofCosModel> multiple;

}


