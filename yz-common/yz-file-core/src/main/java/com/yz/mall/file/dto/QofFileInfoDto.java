package com.yz.mall.file.dto;

import com.yz.mall.file.core.cos.QofCosProperties;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件上传信息
 *
 * @author yunze
 * @since 2025-02-16 15:43:42
 */
@Data
public class QofFileInfoDto implements Serializable {

    private static final long serialVesionUID = 1L;

    /**
     * 文件唯一Id
     * <br>
     * 如果不传fileId，则会自动生成一个id
     * @ignore
     */
    private Long fileId;

    /**
     * 文件名称
     *
     * @mock 靓图.png
     */
    @Length(max = 36, message = "文件名称超过最大长度限制")
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    /**
     * 文件路径（相对路径）
     * <br>
     * 真实存储路径地址为 {@link QofCosProperties#getFilepath()} + {@link QofFileInfoDto#getFilePath()}
     *
     * @mock /business/靓图.png
     */
    @Length(max = 255, message = "文件路径超过最大长度限制")
    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    /**
     * 文件类型
     * <br>
     * 例如image/png、image/jpeg、application/pdf、video/mp4等
     *
     * @mock image/png
     */
    @Length(max = 36, message = "文件类型超过最大长度限制")
    private String fileType;

    /**
     * 文件标签
     * <br>
     * 给文件添加标签（如：证件照、报告、审核表、图标等）
     *
     * @mock 人像
     */
    @Length(max = 36, message = "文件标签超过最大长度限制")
    private String fileLabel;


}

