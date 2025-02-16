package com.yz.mall.file.dto;

import java.time.LocalDateTime;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 系统-文件表(SysFiles)表新增数据模型类
 *
 * @author yunze
 * @since 2025-02-16 15:43:42
 */
@Data
public class SysFilesAddDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    /**
     * 文件路径
     */
    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    /**
     * 文件类型(image/png、image/jpeg)
     */
    @NotBlank(message = "文件类型(image/png、image/jpeg)不能为空")
    private String fileType;

    /**
     * 文件标签
     */
    @NotBlank(message = "文件标签不能为空")
    private String fileLabel;


}

