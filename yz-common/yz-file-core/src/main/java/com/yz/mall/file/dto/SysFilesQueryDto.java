package com.yz.mall.file.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 系统-文件表(SysFiles)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2025-02-16 15:43:42
 */
@Data
public class SysFilesQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型(image/png、image/jpeg)
     */
    private String fileType;

    /**
     * 文件标签
     */
    private String fileLabel;


}

