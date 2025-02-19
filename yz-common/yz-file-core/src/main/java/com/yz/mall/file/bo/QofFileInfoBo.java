package com.yz.mall.file.bo;

import com.yz.mall.file.core.cos.QofCosProperties;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
@Data
public class QofFileInfoBo {

    /**
     * 文件唯一id
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 文件名称
     *
     * @mock 靓图.png
     */
    private String fileName;

    /**
     * 文件存储路径
     * <br>
     * 文件存储路径组成为{@link QofCosProperties#getFilepath()}
     * + {@link com.yz.mall.file.dto.QofFileInfoDto#getDirectoryAddress()}
     * + '/'
     * + {@link QofFileInfoBo#getId()}
     * + ( {@link QofFileInfoBo#getFileName()}的后缀 )
     *
     * @mock /files/business/20250201/1891054775523446784.png
     */
    private String filePath;

    /**
     * 文件类型
     * <br>
     * 例如image/png、image/jpeg、application/pdf、video/mp4等
     *
     * @mock image/png
     */
    private String fileType;

    /**
     * 文件标签
     * <br>
     * 给文件添加标签（如：证件照、报告、审核表、图标等）
     *
     * @mock 人像
     */
    private String fileLabel;

    /**
     * 文件大小(单位byte字节)
     *
     * @mock 1024
     */
    private Long fileSize = 0L;

    
}

