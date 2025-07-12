package com.yz.mall.sys.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
@Data
public class InternalQofFileInfoVo {

    /**
     * 文件唯一Id
     */
    private Long fileId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 文件名称
     * <p>
     * 例如： 靓图.png
     */
    private String fileName;

    /**
     * 文件存储路径
     * <p>
     * 例如： /20250201/1891054775523446784.png
     */
    private String filePath;


    /**
     * 文件类型
     * <br>
     * 例如image/png、image/jpeg、application/pdf、video/mp4等
     * <p>
     * 例如： image/png
     */
    private String fileType;

    /**
     * 文件大小(单位byte字节)
     * <p>
     * 例如： 1024
     */
    private Long fileSize;

    /**
     * 存储别名
     * <br>
     * 对应
     */
    private String fileStorageStation;

    /**
     * 文件存储模式(local、cos、oss)
     */
    private String fileStorageMode;

    /**
     * 预览地址
     */
    private String previewAddress;
}

