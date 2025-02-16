package com.yz.mall.file.core.cos;

import lombok.Data;

/**
 * 腾讯云-COS对象存储的属性配置信息
 * @author yunze
 * @date 2025/2/16 20:39
 */
@Data
public class QofCosModel {

    /**
     * 文件存储路径
     */
    private String filepath;

    /**
     * COS秘钥Id
     */
    private String secretId;

    /**
     * COS秘钥Key
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    public String bucketName;

    /**
     * COS_REGION 参数：配置成存储桶 bucket 的实际地域，例如 ap-guangzhou，更多 COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
     */
    private String region;

}
