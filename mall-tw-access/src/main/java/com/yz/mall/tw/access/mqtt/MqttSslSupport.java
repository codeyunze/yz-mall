package com.yz.mall.tw.access.mqtt;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Paho 单向 TLS：加载 CA 证书（对齐 EMQX Cloud Java 示例思路，不依赖 BouncyCastle）
 */
public final class MqttSslSupport {

    private MqttSslSupport() {
    }

    /**
     * 用 CA 构建 SSLSocketFactory。
     *
     * @param caCertPath 文件系统路径，或 {@code classpath:emqxsl-ca.crt}
     * @return SocketFactory
     */
    public static SSLSocketFactory singleSocketFactory(String caCertPath) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate caCert;
        try (InputStream in = openCaStream(caCertPath); BufferedInputStream bis = new BufferedInputStream(in)) {
            caCert = (X509Certificate) cf.generateCertificate(bis);
        }
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }

    private static InputStream openCaStream(String caCertPath) throws Exception {
        if (caCertPath.startsWith("classpath:")) {
            String resource = caCertPath.substring("classpath:".length()).replaceFirst("^/", "");
            InputStream in = MqttSslSupport.class.getClassLoader().getResourceAsStream(resource);
            if (in == null) {
                throw new IllegalArgumentException("classpath 未找到 CA: " + resource);
            }
            return in;
        }
        return new FileInputStream(caCertPath);
    }
}
