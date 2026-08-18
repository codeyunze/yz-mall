package com.yz.mall.sys;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 第三方开放国密通道配置（服务端密钥仅环境变量/配置中心，禁止硬编码入库）
 *
 * @author yunze
 */
@Data
@ConfigurationProperties(prefix = "yz.mall.sys.open")
public class SysOpenCryptoProperties {

    /**
     * 平台 SM2 公钥 Base64，供第三方下载以加密 SM4 会话密钥
     */
    private String serverPublicKey = "";

    /**
     * 平台 SM2 私钥 Base64（仅机机解密使用；管理端不回显）
     */
    private String serverPrivateKey = "";
}
