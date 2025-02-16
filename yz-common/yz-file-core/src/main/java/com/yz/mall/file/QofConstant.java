package com.yz.mall.file;

/**
 * 常量信息
 *
 * @author yunze
 * @date 2025/2/16 18:04
 */
public class QofConstant {

    /**
     * 项目简称 QOF (Quickly operate files)
     */
    public static final String QOF = "qof";

    /**
     * 启动属性配置名
     */
    public static final String ENABLE = "enable";

    /**
     * 启动属性配置所需值
     */
    public static final String ENABLE_VALUE = "true";

    /**
     * 文件存储类型
     */
    public interface StorageMode {
        String LOCAL = "local";
        String COS = "cos";
        String OSS = "oss";
        String minio = "minio";
    }
}
