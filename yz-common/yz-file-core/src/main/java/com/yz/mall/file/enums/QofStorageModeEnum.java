package com.yz.mall.file.enums;

import com.yz.mall.file.QofConstant;
import lombok.Getter;

/**
 * 文件存储模式
 *
 * @author yunze
 * @date 2025/2/16 16:34
 */
@Getter
public enum QofStorageModeEnum {

    /**
     * 本地存储模式
     */
    LOCAL(QofConstant.StorageMode.LOCAL),
    /**
     * 腾讯云-COS对象存储模式
     */
    COS(QofConstant.StorageMode.COS),
    /**
     * 阿里云-OSS对象存储模式
     */
    OSS(QofConstant.StorageMode.OSS),
    /**
     * Minio-对象存储模式
     */
    MINIO(QofConstant.StorageMode.minio);

    /**
     * 文件存储模式
     */
    private final String mode;

    QofStorageModeEnum(String mode) {
        this.mode = mode;
    }


}
